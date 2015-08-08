package com.duowan.xgame.mobile.service;

public class KeyUtils {

	public  static final int TOKEN_EXPIRE = 30000;
	public  static final String APPID = "mclauncher:v2:";

	public static String getTopSequenceKey(String appId) {
		return APPID + "m:top:key:"+appId;
	}
	
	public  static String getUserAccountKey(String uid) {
		return APPID + "m:account:key:"+uid;
	}
	
	public  static String getDownloadMapsListKey() {
		return APPID + "m:mapdownloadlist:key";
	}
	
	
	public  static String getDownloadMCResListKey(String type) {
		return APPID + "m:resdownloadlist:key:"+type;
	}
	
	public  static String getUserFriendAccountKey(String uid) {
		return APPID + "m:friends:key:"+uid;
	}
	
	public  static String getTokenByUid(String uid) {
		return APPID + "m:token:key:"+uid;
	}
	
	public  static String getNickNameByUid(String uid) {
		return APPID + "m:nickname:key:"+uid;
	}
	
	public  static String getUidByToken(String token) {
		return APPID + "m:uid:key:"+token;
	}
	
	public  static String getUidRefund(String uid,String day) {
		return APPID + "m:ref:key:"+uid+":d:"+day;
	}
	
}
