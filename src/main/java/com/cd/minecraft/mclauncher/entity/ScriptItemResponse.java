package com.cd.minecraft.mclauncher.entity;


import java.util.List;

import com.cd.minecraft.mclauncher.dao.ScriptItems;

/**
 * Created by timo on 15/3/28.
 */
public class ScriptItemResponse {
    List<ScriptItems> plugins;

    public List<ScriptItems> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<ScriptItems> plugins) {
        this.plugins = plugins;
    }
}
