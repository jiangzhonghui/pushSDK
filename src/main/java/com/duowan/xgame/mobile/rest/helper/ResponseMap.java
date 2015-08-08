package com.duowan.xgame.mobile.rest.helper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ResponseMap<T> {

	public Map<String, Object> mapOK(List<T> items) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("total", items.size());
		modelMap.put("response", items);
		modelMap.put("meta", Meta.getMeta(true));

		return modelMap;
	}
	
	public Map<String, Object> mapOK(List<T> items, int pageCount,int pageIndex) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("total", items.size());
		modelMap.put("response", items);
		modelMap.put("meta", Meta.getMeta(true,pageIndex,pageCount));

		return modelMap;
	}
	
	
	
	public Map<String, Object> mapOK(List<T> items, long total) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("total", total);
		modelMap.put("response", items);
		modelMap.put("meta", Meta.getMeta(true));

		return modelMap;
	}
	
	public Map<String, Object> mapOK(List<T> items, String message) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("message", message);
		modelMap.put("response", items);
		modelMap.put("meta", Meta.getMeta(true));

		return modelMap;
	}

	public Map<String, Object> mapOK(T item) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("response", item);
		modelMap.put("meta", Meta.getMeta(true));

		return modelMap;
	}
	
	public Map<String, Object> mapOK(T item, String msg) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("response", item);
		modelMap.put("meta", Meta.getMeta(true,msg));		
		return modelMap;
	}


	public Map<String, Object> mapError(String msg) {

		Map<String, Object> modelMap = new HashMap<String, Object>(2);
		modelMap.put("meta", Meta.getMeta(false,msg));		
		return modelMap;
	}
	
	public Map<String, Object> mapNoAccessToken() {

		Map<String, Object> modelMap = new HashMap<String, Object>(2);
		modelMap.put("message", "no access token");
		modelMap.put("meta", Meta.getMeta(false));		
		return modelMap;
	}
	
	public Map<String, Object> mapNoresponseFound(String msg) {

		Map<String, Object> modelMap = new HashMap<String, Object>(3);
		modelMap.put("message", "no data found");
		modelMap.put("meta", Meta.getMeta(false,msg));

		return modelMap;
	}
	
}
