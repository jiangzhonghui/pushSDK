package com.cd.minecraft.mclauncher.entity;

import com.cd.minecraft.mclauncher.dao.ModItems;

import java.util.List;

/**
 * Created by timo on 15/3/29.
 */
public class ModItemResponse {
    private List<ModItems> mods;

    public List<ModItems> getMods() {
        return mods;
    }

    public void setMods(List<ModItems> mods) {
        this.mods = mods;
    }
}
