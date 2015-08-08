package com.duowan.xgame.mobile.rest.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import com.cd.minecraft.mclauncher.entity.Account;
import com.cd.minecraft.mclauncher.entity.Timeline;
import com.cd.minecraft.mclauncher.entity.TimelineComment;
import com.cd.minecraft.mclauncher.entity.TimelineCommentVo;
import com.cd.minecraft.mclauncher.entity.TimelineVo;
import com.cd.minecraft.mclauncher.repository.AccountRepository;
import com.duowan.xgame.mobile.auth.MD5Generator;
import com.duowan.xgame.mobile.auth.OAuthIssuer;
import com.duowan.xgame.mobile.auth.OAuthIssuerImpl;
import com.duowan.xgame.mobile.common.OAuthSystemException;
import com.duowan.xgame.mobile.model.AccountVo;
import com.duowan.xgame.mobile.rest.helper.ResponseMap;
import com.duowan.xgame.mobile.rest.util.LoggingResponseFilter;
import com.duowan.xgame.mobile.service.KeyUtils;
import com.duowan.xgame.mobile.service.RedisUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
@Path("/timeline")
public class TimelineService {
	
	private static final String keyTimeLine="app:mcpelauncher:top:timeline";
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ResponseMap<Timeline> responseTimelineMapper;
	
	@Autowired
	private ResponseMap<TimelineVo> responseTimelineVoMapper;
	
	@Autowired
	private ResponseMap<TimelineCommentVo> responseTimelineCommentVoMapper;
	
	@Autowired
	private ResponseMap<AccountVo> responseAccountVoMapper;
	
	@Autowired
	private ResponseMap<String> responseStringMapper;
	private final static int LONG_REFRESH_DAY = 3600 * 12;
	private final static int MAX_SHARED_COUNTER_PER_DAY = 2;
	private final static int SCORE_SHARED = 100;
	private final static int SCORE_LIKE = 5;
	private final static int SCORE_COMMENT = 10;
	private final static int SCORE_DOWNLOAD = 5;
	
	private static final String GAMEID = "mcpe";

	@Autowired
	private RedisUtil redisUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(LoggingResponseFilter.class);

	/*
	 * login 需要一起返回用户基本详细
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("retrieve")
	public Map<String, ? extends Object> retrieveTimeLine(@Context HttpHeaders header,@QueryParam("uid") String uid,@QueryParam("pageNumber") int pageNumber,@QueryParam("pageSize") int pageSize) {
		List<TimelineVo> voList = new ArrayList<TimelineVo>();
		String keyUserTimeline =  keyTimeLine ;
		if(uid!=null && uid.length()>0){
			keyUserTimeline = "app:mcpelauncher:top:timeline:"+uid;
		}
		Set<String> keyList = redisUtil.zrevrange(keyUserTimeline, (pageNumber-1)*pageSize, (pageNumber*pageSize)-1);
		for(String key:keyList){
			Timeline item = getTimeline(key);
			if(item!=null){
				AccountVo accountVo = getAccountVo(item.getUid());
				TimelineVo timelineVo = new TimelineVo();
				timelineVo.setNickName(accountVo.getNickName());
				timelineVo.setPicUrl(accountVo.getPicUrl());
				timelineVo.setDesc(item.getDesc());
				//timelineVo.setShareType(item.get);
				timelineVo.setTimelineId(item.getTimelineId());
				timelineVo.setTitle(item.getTitle());
				timelineVo.setUid(item.getUid());
				timelineVo.setImageUrl(item.getImagUrl());
				timelineVo.setDownloadUrl(item.getDownloadUrl());
				timelineVo.setComments(item.getComments());
				timelineVo.setLike(item.getLike());
				timelineVo.setDownload(item.getDownload());
				timelineVo.setTime(item.getTime());
				voList.add(timelineVo);
			}
		}
		return responseTimelineVoMapper.mapOK(voList);
	}
	
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("retrieveComments")
	public Map<String, ? extends Object> retrieveComments(@Context HttpHeaders header,@QueryParam("timelineId") String timelineId,@QueryParam("pageNumber") int pageNumber,@QueryParam("pageSize") int pageSize) {
		String keyTimeLineComment="app:mcpelauncher:top:timeline:"+timelineId+":comment";
		Set<String> keyList = redisUtil.zrevrange(keyTimeLineComment, (pageNumber-1)*pageSize, (pageNumber*pageSize)-1);
		List<TimelineCommentVo> list = new ArrayList<TimelineCommentVo>();
		for(String key:keyList){
			TimelineComment comment = getTimelineComment(key);
			if(comment!=null){
				TimelineCommentVo commentVo =  convertCommentVo(comment);
				list.add(commentVo);
			}
		}
		return responseTimelineCommentVoMapper.mapOK(list);
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("detail")
	public Map<String, ? extends Object> retrieveTimeLineDetail(@Context HttpHeaders header,@QueryParam("timelineId") String timelineId,@QueryParam("pageNumber") int pageNumber,@QueryParam("pageSize") int pageSize) {
		Timeline item = getTimeline(timelineId);
		if(item!=null){
			AccountVo accountVo = getAccountVo(item.getUid());
			TimelineVo timelineVo = new TimelineVo();
			timelineVo.setNickName(accountVo.getNickName());
			timelineVo.setPicUrl(accountVo.getPicUrl());
			timelineVo.setDesc(item.getDesc());
			//timelineVo.setShareType(item.get);
			timelineVo.setTimelineId(item.getTimelineId());
			timelineVo.setTitle(item.getTitle());
			timelineVo.setUid(item.getUid());
			timelineVo.setImageUrl(item.getImagUrl());
			timelineVo.setDownloadUrl(item.getDownloadUrl());
			timelineVo.setComments(item.getComments());
			timelineVo.setLike(item.getLike());
			timelineVo.setDownload(item.getDownload());
			timelineVo.setTime(item.getTime());
			String keyTimeLineComment="app:mcpelauncher:top:timeline:"+timelineId+":comment";
			Set<String> keyList = redisUtil.zrevrange(keyTimeLineComment, (pageNumber-1)*pageSize, (pageNumber*pageSize)-1);
			List<TimelineCommentVo> list = new ArrayList<TimelineCommentVo>();
			for(String key:keyList){
				TimelineComment comment = getTimelineComment(key);
				if(comment!=null){
					TimelineCommentVo commentVo =  convertCommentVo(comment);
					list.add(commentVo);
				}
			}
			timelineVo.setCommentContents(list);
			return responseTimelineVoMapper.mapOK(timelineVo);
		}else{
			return responseTimelineVoMapper.mapError("no data found");
		}
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("sharedPermission")
	public Map<String, ? extends Object> validationSharePermission(@Context HttpHeaders header,@QueryParam("uid") String uid){
		if(avaiableShardCounter(uid)>0){
			AccountVo accountVo = getAccountVo(uid);
			if(accountVo.getCurrentScore()>SCORE_SHARED){
				return responseStringMapper.mapOK("ok");
			}else{
				return responseStringMapper.mapError("not enough score");
			}
		}else{
			return responseStringMapper.mapError("limit counter per day");
		}
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("sharedMap")
	public Map<String, ? extends Object> sharedMap(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("mapTitle") String maptitle,
			@FormParam("mapType") String mapType,
			@FormParam("mapDesc") String mapDesc,
			@FormParam("mapIcon") String mapIcon,
			@FormParam("mapUrl") String mapUrl) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null || !userName.equals(uid)){
				return responseTimelineVoMapper.mapError("no access token");
			}else{
				//Update account cache properties
				AccountVo accountVo = getAccountVo(uid);
				if(accountVo == null){
					return responseTimelineMapper.mapError("account does not exist.");
				}
				if(avaiableShardCounter(uid)<=0){
					return responseTimelineMapper.mapError("limit counter per day");
				}
				if(accountVo.getCurrentScore()<SCORE_SHARED){
					return responseTimelineMapper.mapError("not enough score");
				}
				accountVo.setShared(accountVo.getShared()+1);
				accountVo.setCurrentScore(accountVo.getCurrentScore()-SCORE_SHARED);
				Gson gson = new Gson();
				String cacheResult = gson.toJson(accountVo);
				setAccountVo(uid,cacheResult);
				
				//Update the account's share cache counter
				cacheRefundFeeResult(uid);
				
				//Update the account's score on top list
				String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
				redisUtil.zadd(keyTop, accountVo.getTopScore(), uid);
				
				//save Timeline
				Timeline vo = new Timeline();
				vo.setTitle(maptitle);
				vo.setUid(uid);
				vo.setDesc(mapDesc);
				vo.setImagUrl(mapIcon);
				vo.setDownloadUrl(mapUrl);
				vo.setLike(0);
				vo.setComments(0);
				vo.setDownload(0);
				vo.setTime(System.currentTimeMillis());
				String uuid = newAccessTokenString(uid + "#share#"+ String.valueOf(System.currentTimeMillis()));
				vo.setTimelineId(uuid);
				String timelineCache = gson.toJson(vo);
				this.setTimeline(uuid, timelineCache);
				
				//save id into id public timeline list
				redisUtil.zadd(keyTimeLine, System.currentTimeMillis(), uuid);
				
				//save id into id private timeline list
				String keyUserTimeline =  "app:mcpelauncher:top:timeline:"+uid;
				redisUtil.zadd(keyUserTimeline, System.currentTimeMillis(), uuid);
				
				
				
				return responseTimelineMapper.mapOK(vo);
			}
		}else{
			return responseTimelineMapper.mapError("no access token");
		}
	}
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("like")
	public Map<String, ? extends Object> like(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("timelineId") String itemId) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null || !userName.equals(uid)){
				return responseStringMapper.mapError("no access token");
			}else{
				String totalLikeCount = addShareLikeCounter(uid,itemId);
				return responseStringMapper.mapOK(totalLikeCount);
			}
		}else{
			return responseStringMapper.mapError("no access token");
		}
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("download")
	public Map<String, ? extends Object> download(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("timelineId") String itemId) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null || !userName.equals(uid)){
				return responseStringMapper.mapError("no access token");
			}else{
				String totalLikeCount = addDownloadCounter(itemId);
				return responseStringMapper.mapOK(totalLikeCount);
			}
		}else{
			return responseStringMapper.mapError("no access token");
		}
	}
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("addComment")
	public Map<String, ? extends Object> like(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("timelineId") String timelineId,
			@FormParam("commentText") String commentText) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null || !userName.equals(uid)){
				return responseTimelineCommentVoMapper.mapError("no access token");
			}else{
				TimelineComment comment = addShareCommentsCounter(uid,timelineId,commentText);
				Gson gson = new Gson();
				String cache = gson.toJson(comment);
				setTimelineComment(comment.getCommentId(),cache);
				String keyTimeLineComment="app:mcpelauncher:top:timeline:"+timelineId+":comment";
				redisUtil.zadd(keyTimeLineComment, System.currentTimeMillis(), comment.getCommentId());
				TimelineCommentVo commentVo =  convertCommentVo(comment);
				return responseTimelineCommentVoMapper.mapOK(commentVo);
			}
		}else{
			return responseTimelineCommentVoMapper.mapError("no access token");
		}
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("topuser")
	public Map<String, ? extends Object> getUserInfo(@Context HttpHeaders header,@QueryParam("top") int top) {
		if(top==0){
			top =20;
		}
		List<AccountVo> accountVoList = new ArrayList<AccountVo>();
		String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
		Set<String> topSet = redisUtil.zrevrange(keyTop,0, 20);
		for(String uid:topSet){
			AccountVo accountVo = getAccountVo(uid);
			accountVo.setToken("");
			accountVoList.add(accountVo);
		}
		return responseAccountVoMapper.mapOK(accountVoList);

	}	
	
	private int avaiableShardCounter(String uid) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String day = bartDateFormat.format(date);
		String key = KeyUtils.getUidRefund(uid, day);
		String count = redisUtil.getData(key);
		if (StringUtils.isEmpty(count)) {
			return MAX_SHARED_COUNTER_PER_DAY;
		} else {
			return MAX_SHARED_COUNTER_PER_DAY-Integer.parseInt(count);
		}
	}
	
	private void cacheRefundFeeResult(String uid) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String day = bartDateFormat.format(date);
		String key = KeyUtils.getUidRefund(uid, day);
		String count = redisUtil.getData(key);
		if (StringUtils.isEmpty(count)) {
			redisUtil.setData(key, String.valueOf(MAX_SHARED_COUNTER_PER_DAY-1), LONG_REFRESH_DAY);
		} else {
			redisUtil.setData(key, String.valueOf(Integer.parseInt(count) + 1),LONG_REFRESH_DAY);
		}
	}
	
	
	private TimelineCommentVo convertCommentVo(TimelineComment timelineComment){
		TimelineCommentVo commentVo = new TimelineCommentVo();
		AccountVo accountVo= getAccountVo(timelineComment.getUid());
		commentVo.setNickName(accountVo.getNickName());
		commentVo.setPicUrl(accountVo.getPicUrl());
		commentVo.setCommentId(timelineComment.getCommentId());
		commentVo.setCommentText(timelineComment.getCommentText());
		commentVo.setTime(timelineComment.getTime());
		commentVo.setUid(timelineComment.getUid());
		commentVo.setTimelineId(timelineComment.getTimelineId());
		return commentVo;
	}

	
	private String addShareLikeCounter(String uid,String timelineId){
		Timeline vo = getTimeline(timelineId);
		if(vo!=null){
			Gson gson = new Gson();
			
			//add download score refund for Timeline's author
			AccountVo accountVo = getAccountVo(uid);
			if(accountVo != null){
				accountVo.setCurrentScore(accountVo.getCurrentScore()+SCORE_LIKE);
				String cacheResult = gson.toJson(accountVo);
				setAccountVo(uid,cacheResult);
				String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
				redisUtil.zadd(keyTop, accountVo.getTopScore(), uid);
			}
			vo.setLike(vo.getLike()+1);
			String cache = gson.toJson(vo);
			setTimeline(timelineId,cache);
			String likeCount = String.valueOf(vo.getLike());
			return likeCount;
		}else{
			return "0";
		}
		
	}
	
	private String addDownloadCounter(String timelineId){
		Timeline vo = getTimeline(timelineId);
		if(vo!=null){
			Gson gson = new Gson();
			
			//add download score refund for Timeline's author
			AccountVo accountVo = getAccountVo(vo.getUid());
			if(accountVo != null){
				accountVo.setCurrentScore(accountVo.getCurrentScore()+SCORE_DOWNLOAD);
				String cacheResult = gson.toJson(accountVo);
				setAccountVo(vo.getUid(),cacheResult);
				String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
				redisUtil.zadd(keyTop, accountVo.getTopScore(), vo.getUid());
			}
			vo.setDownload(vo.getDownload()+1);
			String cache = gson.toJson(vo);
			setTimeline(timelineId,cache);
			String likeCount = String.valueOf(vo.getDownload());
			return likeCount;
		}else{
			return "0";
		}
		
	}
	
	private TimelineComment addShareCommentsCounter(String uid,String timelineId,String comments){
		Timeline vo = getTimeline(timelineId);
		if(vo!=null){
			vo.setComments(vo.getComments()+1);
			Gson gson = new Gson();
			
			//add comment score refund for comment's author
			AccountVo accountVo = getAccountVo(uid);
			if(accountVo != null){
				accountVo.setCurrentScore(accountVo.getCurrentScore()+SCORE_COMMENT);
				String cacheResult = gson.toJson(accountVo);
				setAccountVo(uid,cacheResult);
				String keyTop = KeyUtils.getTopSequenceKey(GAMEID);
				redisUtil.zadd(keyTop, accountVo.getTopScore(), uid);
			}
			
			//save comments
			String cache = gson.toJson(vo);
			setTimeline(timelineId,cache);
			TimelineComment comment = new TimelineComment();
			comment.setCommentText(comments);
			comment.setTimelineId(timelineId);
			comment.setTime(System.currentTimeMillis());
			comment.setUid(uid);
			String uuid = newAccessTokenString(timelineId+"#"+uid+ "#comment#"+ String.valueOf(System.currentTimeMillis()));
			comment.setCommentId(uuid);
			return comment;
		}else{
			return null;
		}
	}
	
	public AccountVo getAccountVo(String uid){
		String accountInfoKey = "app:mcpelauncher:account:uid:"+uid;
		String cacheResult = redisUtil.getData(accountInfoKey);
		AccountVo vo=null;
		if(!StringUtils.isEmpty(cacheResult)){
			Gson gson = new Gson();
			vo = gson.fromJson(cacheResult,
					new TypeToken<AccountVo>() {
					}.getType());
		}
		return vo;
	}
	
	public void setAccountVo(String uid,String cache){
		String accountInfoKey = "app:mcpelauncher:account:uid:"+uid;
		redisUtil.setData(accountInfoKey,cache);
	}
	
	
	public Timeline getTimeline(String timelineId){
		String key = "app:mcpelauncher:timeline:id:"+timelineId;
		String cacheResult = redisUtil.getData(key);
		Timeline vo=null;
		if(!StringUtils.isEmpty(cacheResult)){
			Gson gson = new Gson();
			vo = gson.fromJson(cacheResult,
					new TypeToken<Timeline>() {
					}.getType());
		}
		return vo;
	}
	
	public TimelineComment getTimelineComment(String commentId){
		String key = "app:mcpelauncher:commentId:id:"+commentId;
		String cacheResult = redisUtil.getData(key);
		TimelineComment vo=null;
		if(!StringUtils.isEmpty(cacheResult)){
			Gson gson = new Gson();
			vo = gson.fromJson(cacheResult,
					new TypeToken<TimelineComment>() {
					}.getType());
		}
		return vo;
	}
	
	public void setTimeline(String timelineId,String cache){
		String key = "app:mcpelauncher:timeline:id:"+timelineId;
		redisUtil.setData(key,cache);
	}
	
	
	public void setTimelineComment(String commentId,String cache){
		String key = "app:mcpelauncher:commentId:id:"+commentId;
		redisUtil.setData(key,cache);
	}
	
	private String newAccessTokenString(String deviceUUID) {
		String accessToken = "";
		try {
			OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator(
					deviceUUID));
			accessToken = issuer.accessToken();
		} catch (OAuthSystemException e) {
			accessToken = "";
			logger.info("Can not genreateion AccessToken" + e.getMessage());
			e.printStackTrace();
		}
		return accessToken;
	}
	
}

