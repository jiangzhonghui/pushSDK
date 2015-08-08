package com.cd.minecraft.mclauncher.entity;

import java.util.List;

import com.cd.minecraft.mclauncher.dao.ScriptItems;
import com.duowan.xgame.mobile.model.MapItem;

public class MapItemResponse {
	 List<MapItem> maps;

	public List<MapItem> getMaps() {
		return maps;
	}

	public void setMaps(List<MapItem> maps) {
		this.maps = maps;
	}
	 
}
