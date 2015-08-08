package com.duowan.xgame.mobile.rest.helper;

import java.io.Serializable;

public class Meta implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7644582570282369567L;
	
	private int code;
	private String msg;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public static Meta getMeta(boolean result, String message){
		
		Meta meta=new Meta();
		if(result){
			meta.setCode(200);
		}else{
			meta.setCode(400);
		}
		meta.setMsg(message);
		return meta;
	}
	
	public static Meta getMeta(boolean result){
		
		Meta meta=new Meta();
		if(result){
			meta.setCode(200);
		}else{
			meta.setCode(400);
		}
		return meta;
	}
	
	public static Meta getMeta(boolean result, int pageIndex, int pageCount){
		
		Meta meta=new Meta();
		if(result){
			meta.setCode(200);
		}else{
			meta.setCode(400);
		}
		return meta;
	}
	
	public static Meta getMeta(boolean result, String message, String exceptionType){
		
		Meta meta=new Meta();
		if(result){
			meta.setCode(200);
		}else{
			meta.setCode(400);
		}
		meta.setMsg(message);
		return meta;
	}
		
}