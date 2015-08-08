package com.duowan.xgame.mobile.rest.service;

import java.util.Map;

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

import push.PushService;

import com.baidu.yun.push.sample.IOSPushNotificationToAll;
import com.cd.minecraft.mclauncher.services.AndroidPushMsg;
import com.cd.minecraft.mclauncher.services.IOSPushNotification;
import com.duowan.xgame.mobile.rest.helper.ResponseMap;
import com.duowan.xgame.mobile.rest.util.LoggingResponseFilter;
import com.duowan.xgame.mobile.service.RedisUtil;

@Component
@Path("/tspushserver")
public class TsPushServer {
	
	@Autowired
	private RedisUtil redisUtil;
	
	@Autowired
	private ResponseMap<String> responseMapper;
	
	private static final Logger logger = LoggerFactory.getLogger(LoggingResponseFilter.class);
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=UTF-8"})
	@Path("push")
	public Map<String, ? extends Object> pushMessage(@Context HttpHeaders header,@QueryParam("message") String message) {
		
		try{
			IOSPushNotification.sendMessageToAll(message);
			AndroidPushMsg.sendMessageToAll(message);
			return responseMapper.mapOK("ok");
		}catch(Exception ex){
			return responseMapper.mapError(ex.getMessage().toString());
		}
		
	}
	
}
