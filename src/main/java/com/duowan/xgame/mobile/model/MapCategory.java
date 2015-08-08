package com.duowan.xgame.mobile.model;

import java.util.List;

/**
 * Created by Administrator on 2015-03-06.
 */
public class MapCategory {

    private String minVersion;
    private String maxVersion;
    private String lastUpdate;
    private List<DailyMapItem> data;

    public String getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(String minVersion) {
        this.minVersion = minVersion;
    }

    public String getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(String maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<DailyMapItem> getData() {
        return data;
    }

    public void setData(List<DailyMapItem> data) {
        this.data = data;
    }
}
