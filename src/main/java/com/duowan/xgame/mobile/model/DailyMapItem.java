package com.duowan.xgame.mobile.model;

import java.util.List;

/**
 * Created by Administrator on 2015-03-06.
 */
public class DailyMapItem {
    private String updateDate;
    private List<MapItem> items;

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public List<MapItem> getItems() {
        return items;
    }

    public void setItems(List<MapItem> items) {
        this.items = items;
    }
}
