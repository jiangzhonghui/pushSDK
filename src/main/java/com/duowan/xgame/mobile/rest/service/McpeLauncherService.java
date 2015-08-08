package com.duowan.xgame.mobile.rest.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cd.minecraft.mclauncher.dao.ScriptItems;
import com.cd.minecraft.mclauncher.entity.MapItemResponse;
import com.cd.minecraft.mclauncher.entity.ModItemResponse;
import com.cd.minecraft.mclauncher.entity.ScriptItemResponse;
import com.cd.minecraft.mclauncher.entity.ServerItemsResponse;
import com.cd.minecraft.mclauncher.entity.SkinItemResponse;
import com.cd.minecraft.mclauncher.entity.TextureItemResponse;
import com.duowan.xgame.mobile.model.AccountVo;
import com.duowan.xgame.mobile.model.DailyMapItem;
import com.duowan.xgame.mobile.model.MapCategory;
import com.duowan.xgame.mobile.model.MapItem;
import com.duowan.xgame.mobile.model.WeihuiToken;
import com.duowan.xgame.mobile.rest.helper.HttpRequest;
import com.duowan.xgame.mobile.rest.helper.ResponseMap;
import com.duowan.xgame.mobile.rest.util.LoggingResponseFilter;
import com.duowan.xgame.mobile.service.KeyUtils;
import com.duowan.xgame.mobile.service.RedisUtil;
import com.duowan.xgame.mobile.util.MD5;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
@Path("/mcpelauncher")
public class McpeLauncherService {

	private final static String URL_DOWNLOAD_MAP = "http://mc.tuboshu.com/resources/app-config/maps.json";
	private final static String URL_DOWNLOAD_PLUGINS = "http://mclaunchercdn.qiniudn.com/plugins.json";
	private final static String URL_DOWNLOAD_SKINS = "http://mclaunchercdn.qiniudn.com/skins.json";
	private final static String URL_DOWNLOAD_TEXTURE = "http://mclaunchercdn.qiniudn.com/texture.json";
	private final static String URL_DOWNLOAD_MOD = "http://mclaunchercdn.qiniudn.com/mod.json";
	private final static String URL_DOWNLOAD_MAPS_QINIU = "http://mclaunchercdn.qiniudn.com/maps.json";
	private final static String URL_DOWNLOAD_SERVER = "http://mclaunchercdn.qiniudn.com/server.json";
	
	@Autowired
	private ResponseMap<MapCategory> responseMapper;
	
	@Autowired
	private ResponseMap<AccountVo> responseAccountMapper;

	@Autowired
	private ResponseMap<ScriptItemResponse> responseScriptMapper;

	@Autowired
	private ResponseMap<TextureItemResponse> responseTextureMapper;

	@Autowired
	private ResponseMap<SkinItemResponse> responseSkinMapper;

	@Autowired
	private ResponseMap<ModItemResponse> responseModMapper;
	@Autowired
	private ResponseMap<ServerItemsResponse> responseServerMapper;
	

	@Autowired
	private RedisUtil redisUtil;

	private final static int LONG_REFRESH_DAY = 3600 * 12;

	private static final Logger logger = LoggerFactory
			.getLogger(LoggingResponseFilter.class);

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("getdownloadmaps")
	public Map<String, ? extends Object> getDownloadMapsList(
			@Context HttpHeaders header,
			@QueryParam(value = "access_token") String accessToken,
			@QueryParam(value = "lastId") int lastId,
			@QueryParam(value = "pageSize") int pageSize,
			@QueryParam(value = "uid") String uid,
			@QueryParam(value = "sign") String sign)
			throws UnsupportedEncodingException {
		String keyMapList = KeyUtils.getDownloadMapsListKey();
		String mapData = redisUtil.getData(keyMapList);
		MapCategory mapCategory;
		Gson gson = new Gson();
		if (StringUtils.hasText(mapData)) {
			mapCategory = gson.fromJson(mapData, new TypeToken<MapCategory>() {
			}.getType());
		} else {
			mapCategory = loadDownloadMapDate();
			if (mapCategory != null) {
				List<DailyMapItem> dList = mapCategory.getData();
				
				for (DailyMapItem dItem : dList) {
					List<MapItem> deleteMap = new ArrayList<MapItem>();
					for (MapItem item : dItem.getItems()) {
						if(item.getDownloadUrl().equals("http://pkg.tuboshu.com/common/mc/map/公告.zip") || item.getId()==272 || item.getId()==277  || item.getTitle().contains("多玩盒子更新") || item.getType().contains("公告地图")){
							deleteMap.add(item);
							continue;
						}
						item.setDetail(item.getDetail().replace("地图说明：\n", "")
								.replace("禁止其他APP转载", "")
								.replace("未经作者允许", "")
								.replace("已授权发布", "")
								.replace("未经作者和多玩允许", "")
								.replace("已独家授权发布", "")
								.replace("未经作者和多玩允许。", "")
								.replace("多玩我的世界盒子", "").replace("作者已授权", "")
								.replace("多玩", "").replace("盒子", "")
								.replace("已独家授权多玩我的世界盒子发布，未经作者和多玩允许，禁止其他APP转载。", "")
								.replace("3、盒子独家地图专题，知名工作室入驻！", "")
								.replace("发布该地图作品，", "").replace("严禁转载", ""));
					}
					
					if(deleteMap.size()>0){
						for(MapItem item:deleteMap)
						dItem.getItems().remove(item);
					}
				}
				MapItemResponse response = loadMapData();
				if(response!=null && response.getMaps()!=null){
					mapCategory.getData().get(mapCategory.getData().size()-1).getItems().addAll(response.getMaps());
				}
				String cacheResult = gson.toJson(mapCategory);
				redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			}
			if (mapCategory == null) {
				mapCategory = new MapCategory();
			} else {
				String cacheResult = gson.toJson(mapCategory);
				redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			}
		}
		return responseMapper.mapOK(mapCategory);
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("syncdata")
	public Map<String, ? extends Object> syncData() {
		MapCategory mapCategory = loadDownloadMapDate();
		Gson gson = new Gson();
		String cacheResult = "";
		if (mapCategory != null) {
			String keyMapList = KeyUtils.getDownloadMapsListKey();
			List<DailyMapItem> dList = mapCategory.getData();
			for (DailyMapItem dItem : dList) {
				List<MapItem> deleteMap = new ArrayList<MapItem>();
				for (MapItem item : dItem.getItems()) {
					if(item.getDownloadUrl().equals("http://pkg.tuboshu.com/common/mc/map/公告.zip") || item.getId()==272 || item.getId()==277  || item.getTitle().contains("多玩盒子更新") || item.getType().contains("公告地图")){
						deleteMap.add(item);
						continue;
					}
					item.setDetail(item.getDetail().replace("地图说明：\n", "")
							.replace("禁止其他APP转载", "")
							.replace("未经作者允许", "")
							.replace("已授权发布", "")
							.replace("未经作者和多玩允许", "")
							.replace("已独家授权发布", "")
							.replace("未经作者和多玩允许。", "")
							.replace("多玩我的世界盒子", "").replace("作者已授权", "")
							.replace("多玩", "").replace("盒子", "")
							.replace("已独家授权多玩我的世界盒子发布，未经作者和多玩允许，禁止其他APP转载。", "")
							.replace("3、盒子独家地图专题，知名工作室入驻！", "")
							.replace("发布该地图作品，", "").replace("严禁转载", ""));
				}
				if(deleteMap.size()>0){
					for(MapItem item:deleteMap)
					dItem.getItems().remove(item);
				}
			}
			
			MapItemResponse response = loadMapData();
			if(response!=null && response.getMaps()!=null){
				mapCategory.getData().get(mapCategory.getData().size()-1).getItems().addAll(response.getMaps());
			}
			cacheResult = gson.toJson(mapCategory);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
		}

		String keyMapList = KeyUtils.getDownloadMCResListKey("plugins");
		ScriptItemResponse data = this.loadScriptDate();
		if (data != null) {
			cacheResult = gson.toJson(data);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
		}

		keyMapList = KeyUtils.getDownloadMCResListKey("texture");
		TextureItemResponse dataTexture = this.loadTextureDate();
		if (dataTexture != null) {
			cacheResult = gson.toJson(dataTexture);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
		}
		
		
		keyMapList = KeyUtils.getDownloadMCResListKey("server");
		ServerItemsResponse dataServer = this.loadServerData();
		if (dataServer != null) {
			cacheResult = gson.toJson(dataServer);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
		}

		keyMapList = KeyUtils.getDownloadMCResListKey("skin");
		SkinItemResponse dataSkin = this.loadSkinDate();
		if (dataSkin != null) {
			cacheResult = gson.toJson(dataSkin);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
		}

		keyMapList = KeyUtils.getDownloadMCResListKey("mod");
		ModItemResponse dataMod = this.loadModDate();
		if (dataMod != null) {
			cacheResult = gson.toJson(dataMod);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
		}

		Map<String, Object> config = new HashMap<String, Object>();
		config.put("done", 0);
		return config;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("refreshdownloadmaps")
	public Map<String, ? extends Object> refreshDownloadMapsList(
			@Context HttpHeaders header) throws UnsupportedEncodingException {
		MapCategory mapCategory = loadDownloadMapDate();
		if (mapCategory != null) {
			String keyMapList = KeyUtils.getDownloadMapsListKey();
			List<DailyMapItem> dList = mapCategory.getData();
			
			for (DailyMapItem dItem : dList) {
				List<MapItem> deleteMap = new ArrayList<MapItem>();
				for (MapItem item : dItem.getItems()) {
					if(item.getDownloadUrl().equals("http://pkg.tuboshu.com/common/mc/map/公告.zip") || item.getId()==272 || item.getId()==277  || item.getTitle().contains("多玩盒子更新") || item.getType().contains("公告地图")){
						deleteMap.add(item);
						continue;
					}
					item.setDetail(item.getDetail().replace("地图说明：\n", "")
							.replace("禁止其他APP转载", "")
							.replace("未经作者允许", "")
							.replace("已授权发布", "")
							.replace("未经作者和多玩允许", "")
							.replace("已独家授权发布", "")
							.replace("未经作者和多玩允许。", "")
							.replace("多玩我的世界盒子", "").replace("作者已授权", "")
							.replace("多玩", "").replace("盒子", "")
							.replace("已独家授权多玩我的世界盒子发布，未经作者和多玩允许，禁止其他APP转载。", "")
							.replace("3、盒子独家地图专题，知名工作室入驻！", "")
							.replace("发布该地图作品，", "").replace("严禁转载", ""));
				}
				if(deleteMap.size()>0){
					for(MapItem item:deleteMap)
					dItem.getItems().remove(item);
				}
			}
			MapItemResponse response = loadMapData();
			if(response!=null && response.getMaps()!=null){
				mapCategory.getData().get(mapCategory.getData().size()-1).getItems().addAll(response.getMaps());
			}
			Gson gson = new Gson();
			String cacheResult = gson.toJson(mapCategory);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			return responseMapper.mapOK(mapCategory);
		} else {
			return responseMapper.mapError("not data for refresh");
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("appconfig")
	public Map<String, ? extends Object> getAppConfig(
			@Context HttpHeaders header,
			@QueryParam(value = "channel") String channel)
			throws UnsupportedEncodingException {

		if (channel.equals("domob")) {
			Map<String, Object> config = new HashMap<String, Object>();
			config.put("bannerAd", 1);
			config.put("splashAd", 1);
			config.put("inertAd", 1);
			config.put("inertAdCount", 4);
			return config;
		} else {
			Map<String, Object> config = new HashMap<String, Object>();
			config.put("bannerAd", 0);
			config.put("splashAd", 0);
			config.put("inertAd", 0);
			config.put("inertAdCount", 4);
			return config;
		}

	}
	
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("plugins")
	public Map<String, ? extends Object> getPlugins(
			@Context HttpHeaders header,
			@QueryParam(value = "channel") String channel)
			throws UnsupportedEncodingException {

		String keyMapList = KeyUtils.getDownloadMCResListKey("plugins");
		String cacheResult = redisUtil.getData(keyMapList);
		Gson gson = new Gson();
		if (!StringUtils.isEmpty(cacheResult)) {
			ScriptItemResponse data = gson.fromJson(cacheResult,
					new TypeToken<ScriptItemResponse>() {
					}.getType());
			return responseScriptMapper.mapOK(data);
		} else {
			ScriptItemResponse data = this.loadScriptDate();
			cacheResult = gson.toJson(data);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			return responseScriptMapper.mapOK(data);
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("texture")
	public Map<String, ? extends Object> getTexture(
			@Context HttpHeaders header,
			@QueryParam(value = "channel") String channel)
			throws UnsupportedEncodingException {

		String keyMapList = KeyUtils.getDownloadMCResListKey("texture");
		String cacheResult = redisUtil.getData(keyMapList);
		Gson gson = new Gson();
		if (!StringUtils.isEmpty(cacheResult)) {
			TextureItemResponse data = gson.fromJson(cacheResult,
					new TypeToken<TextureItemResponse>() {
					}.getType());
			return responseTextureMapper.mapOK(data);
		} else {
			TextureItemResponse data = this.loadTextureDate();
			cacheResult = gson.toJson(data);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			return responseTextureMapper.mapOK(data);
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("skin")
	public Map<String, ? extends Object> getSkin(@Context HttpHeaders header,
			@QueryParam(value = "channel") String channel)
			throws UnsupportedEncodingException {

		String keyMapList = KeyUtils.getDownloadMCResListKey("skin");
		String cacheResult = redisUtil.getData(keyMapList);
		Gson gson = new Gson();
		if (!StringUtils.isEmpty(cacheResult)) {
			SkinItemResponse data = gson.fromJson(cacheResult,
					new TypeToken<SkinItemResponse>() {
					}.getType());
			return responseSkinMapper.mapOK(data);
		} else {
			SkinItemResponse data = this.loadSkinDate();
			cacheResult = gson.toJson(data);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			return responseSkinMapper.mapOK(data);
		}
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("mod")
	public Map<String, ? extends Object> getMod(@Context HttpHeaders header,
			@QueryParam(value = "channel") String channel)
			throws UnsupportedEncodingException {

		String keyMapList = KeyUtils.getDownloadMCResListKey("mod");
		String cacheResult = redisUtil.getData(keyMapList);
		Gson gson = new Gson();
		if (!StringUtils.isEmpty(cacheResult)) {
			ModItemResponse data = gson.fromJson(cacheResult,
					new TypeToken<ModItemResponse>() {
					}.getType());
			return responseModMapper.mapOK(data);
		} else {
			ModItemResponse data = this.loadModDate();
			cacheResult = gson.toJson(data);
			redisUtil.setData(keyMapList, cacheResult, LONG_REFRESH_DAY);
			return responseModMapper.mapOK(data);
		}
	}
	
	

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8" })
	@Path("server")
	public Map<String, ? extends Object> getServer(@Context HttpHeaders header,
			@QueryParam(value = "channel") String channel)
			throws UnsupportedEncodingException {

		String keyMapList = KeyUtils.getDownloadMCResListKey("server");
		String cacheResult = redisUtil.getData(keyMapList);
		Gson gson = new Gson();
		if (!StringUtils.isEmpty(cacheResult)) {
			ServerItemsResponse data = gson.fromJson(cacheResult,
					new TypeToken<ServerItemsResponse>() {
					}.getType());
			return responseServerMapper.mapOK(data);
		} else {
			ServerItemsResponse data = this.loadServerData();
			cacheResult = gson.toJson(data);
			redisUtil.setData(keyMapList, cacheResult);
			return responseServerMapper.mapOK(data);
		}
	}

	public static ScriptItemResponse loadScriptDate() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_PLUGINS+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			ScriptItemResponse data = gson.fromJson(jsonString,
					new TypeToken<ScriptItemResponse>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}
	
	public static MapItemResponse loadMapData() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_MAPS_QINIU+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			MapItemResponse data = gson.fromJson(jsonString,
					new TypeToken<MapItemResponse>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}

	public static TextureItemResponse loadTextureDate() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_TEXTURE+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			TextureItemResponse data = gson.fromJson(jsonString,
					new TypeToken<TextureItemResponse>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}

	public static SkinItemResponse loadSkinDate() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_SKINS+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			SkinItemResponse data = gson.fromJson(jsonString,
					new TypeToken<SkinItemResponse>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}

	public static ModItemResponse loadModDate() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_MOD+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			ModItemResponse data = gson.fromJson(jsonString,
					new TypeToken<ModItemResponse>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}
	
	public static ServerItemsResponse loadServerData() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_SERVER+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			ServerItemsResponse data = gson.fromJson(jsonString,
					new TypeToken<ServerItemsResponse>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}

	public static MapCategory loadDownloadMapDate() {
		String jsonString = "";
		try {
			jsonString = HttpRequest.get(URL_DOWNLOAD_MAP+"?id="+String.valueOf((new Random()).nextInt()))
					.accept("application/json").body();
		} catch (Exception ex) {
			jsonString = "";
		}
		if (jsonString.length() > 0) {
			Gson gson = new Gson();
			MapCategory data = gson.fromJson(jsonString,
					new TypeToken<MapCategory>() {
					}.getType());
			return data;
		} else {
			return null;
		}
	}

	private boolean validateMd5Sign(String data, String sign) {
		data = data + "&privatekey=900150983cd24fb0d6963f7d28e17f72";
		logger.info("sign:" + sign);
		logger.info("data:" + data);
		String md5Str = MD5.md5Digest(data);
		logger.info("md5:" + md5Str);
		if (md5Str.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

}
