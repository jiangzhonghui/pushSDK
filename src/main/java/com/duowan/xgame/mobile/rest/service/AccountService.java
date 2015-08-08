package com.duowan.xgame.mobile.rest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.cd.minecraft.mclauncher.entity.Account;
import com.cd.minecraft.mclauncher.entity.Timeline;
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
@Path("/account")
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	
	@Autowired
	private ResponseMap<AccountVo> responseMapper;
	
	@Autowired
	private ResponseMap<String> responseStringMapper;
	
	private static final int GIVE_SCORE= 1000;
	@Autowired
	private RedisUtil redisUtil;
	
	private static final Logger logger = LoggerFactory.getLogger(LoggingResponseFilter.class);

	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("login")
	public Map<String, ? extends Object> signin(@Context HttpHeaders header,
			@FormParam("uid") String uid, 
			@FormParam("password") String password,
			@FormParam("uuid") String uuid) {
		Account account = accountRepository.findByUid(uid);
		if(account!=null){
			if(account.getPassword().equals( password)){
				String token= newAccessTokenString(uid  + ":" +uuid);
				redisUtil.setData("app:mcpelauncher:uid:"+uid +":token", token);
				redisUtil.setData("app:mcpelauncher:token:"+token +":uid", uid);
				account.setToken(token);
				accountRepository.save(account);
				AccountVo vo = getAccountVo(uid);
				if(vo==null){
					vo = new AccountVo();
					vo.setUid(uuid);
					vo.setToken(account.getToken());
					vo.setRoleName(account.getRole());
					vo.setNickName(account.getNickName());
					vo.setPicUrl(account.getPicUrl());
					vo.setCurrentScore(GIVE_SCORE);
					vo.setLevel(0);
					vo.setShared(0);
					vo.setRefundCounter(0);
					Gson gson = new Gson();
					String cacheResult = gson.toJson(vo);
					setAccountVo(uid, cacheResult);				
				}else{
					vo.setToken(account.getToken());
				}
				return responseMapper.mapOK(vo);
			}else{
				return responseMapper.mapError("wrong password");
			}
		}else{
			return responseMapper.mapError("no account found");
		}
		
	}
	
			
	private String newAccessTokenString(String deviceUUID){
		String accessToken ="";
		try {
			OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator(deviceUUID));
			accessToken = issuer.accessToken();
		} catch (OAuthSystemException e) {
			accessToken="";
			logger.error("Can not genreateion AccessToken" + e.getMessage());
			e.printStackTrace();
		}
		return accessToken;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("register")
	public Map<String, ? extends Object> registerAccount(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("nickname") String nickname,
			@FormParam("picurl") String picUrl,
			@FormParam("password") String password) {		
		Account account = accountRepository.findByUid(uid);
		if(account != null){
			return responseStringMapper.mapError("account exist");
		}else{
			account=new Account();
			account.setNickName(nickname);
			account.setPassword(password);
			account.setUid(uid);
			account.setPicUrl(picUrl);
			account.setRole("");
			accountRepository.save(account);
			AccountVo vo = new AccountVo();
			vo.setUid(uid);
			vo.setToken("");
			vo.setRoleName(account.getRole());
			vo.setNickName(account.getNickName());
			vo.setPicUrl(account.getPicUrl());
			vo.setCurrentScore(GIVE_SCORE);
			vo.setLevel(0);
			vo.setShared(0);
			vo.setRefundCounter(0);
			Gson gson = new Gson();
			String cacheResult = gson.toJson(vo);
			setAccountVo(uid, cacheResult);			
			
			return responseStringMapper.mapOK("ok");
		}
	}
	
	/*
	 * 修改账号密码
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("updatepassword")
	public Map<String, ? extends Object> updateAccountPassword(@Context HttpHeaders header,
			@FormParam("oldpassword") String oldpassword,
			@FormParam("newpassword") String newpassword,
			@FormParam("uid") String uid) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null){
				return responseStringMapper.mapError("no access token");
			}else{
				Account account = accountRepository.findByUid(uid);
				if(account == null){
					return responseStringMapper.mapError("account does not exist.");
				}
				if(account.getPassword().equals(oldpassword)){
					account.setPassword(newpassword);
					account = accountRepository.save(account);
					return responseStringMapper.mapOK("ok");
				}else{
					return responseStringMapper.mapError("old password is wrong.");
				}
			}
		}else{
			return responseStringMapper.mapError("no access token");
		}
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("updatenickname")
	public Map<String, ? extends Object> updateNickName(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("nickname") String nickname) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null || !userName.equals(uid)){
				return responseStringMapper.mapError("no access token");
			}else{
				Account account = accountRepository.findByUid(uid);
				if(account == null){
					return responseStringMapper.mapError("account does not exist.");
				}
				account.setNickName(nickname);
				account = accountRepository.save(account);
				AccountVo vo = getAccountVo(uid);
				if(vo==null){
					vo = new AccountVo();
					vo.setUid(uid);
					vo.setToken(account.getToken());
					vo.setRoleName(account.getRole());
					vo.setNickName(account.getNickName());
					vo.setPicUrl(account.getPicUrl());
					vo.setCurrentScore(0);
					vo.setLevel(0);
					vo.setShared(0);
					vo.setRefundCounter(0);
					Gson gson = new Gson();
					String cacheResult = gson.toJson(vo);
					setAccountVo(uid, cacheResult);				
				}else{
					vo.setNickName(account.getNickName());
					Gson gson = new Gson();
					String cacheResult = gson.toJson(vo);
					setAccountVo(uid, cacheResult);				
				}
				return responseStringMapper.mapOK("ok");
			}
		}else{
			return responseStringMapper.mapError("no access token");
		}
	}	
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	@Produces({ MediaType.APPLICATION_JSON})
	@Path("updatepic")
	public Map<String, ? extends Object> updatePic(@Context HttpHeaders header,
			@FormParam("uid") String uid,
			@FormParam("picurl") String picurl) {		
		String accessToken = header.getHeaderString("access_token");
		if(accessToken!=null){
			String redisKey = "app:mcpelauncher:token:"+accessToken +":uid";
			String userName = redisUtil.getData(redisKey);
			if(userName==null || !userName.equals(uid)){
				return responseStringMapper.mapError("no access token");
			}else{
				Account account = accountRepository.findByUid(uid);
				if(account == null){
					return responseStringMapper.mapError("account does not exist.");
				}
				account.setPicUrl(picurl);
				account = accountRepository.save(account);
				AccountVo vo = getAccountVo(uid);
				if(vo==null){
					vo = new AccountVo();
					vo.setUid(uid);
					vo.setToken(account.getToken());
					vo.setRoleName(account.getRole());
					vo.setNickName(account.getNickName());
					vo.setPicUrl(account.getPicUrl());
					vo.setCurrentScore(0);
					vo.setLevel(0);
					vo.setShared(0);
					vo.setRefundCounter(0);
					Gson gson = new Gson();
					String cacheResult = gson.toJson(vo);
					setAccountVo(uid, cacheResult);				
				}else{
					vo.setPicUrl(account.getPicUrl());
					Gson gson = new Gson();
					String cacheResult = gson.toJson(vo);
					setAccountVo(uid, cacheResult);				
				}
				return responseStringMapper.mapOK("ok");
			}
		}else{
			return responseStringMapper.mapError("no access token");
		}
	}
	
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("getuserinfo")
	public Map<String, ? extends Object> getUserInfo(@Context HttpHeaders header,@QueryParam("uid") String uid) {
		if(uid!=null && uid.length()>0){
			AccountVo accountVo = getAccountVo(uid);
			accountVo.setToken("");
			return responseMapper.mapOK(accountVo);
		}else{
			return responseMapper.mapError("no uid info");
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
	
}
