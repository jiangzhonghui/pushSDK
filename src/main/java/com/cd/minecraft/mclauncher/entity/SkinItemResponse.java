package com.cd.minecraft.mclauncher.entity;

import com.cd.minecraft.mclauncher.dao.SkinItems;

import java.util.List;

/**
 * Created by timo on 15/3/29.
 */
public class SkinItemResponse {

    private List<SkinItems> skins;

    public List<SkinItems> getSkins() {
        return skins;
    }

    public void setSkins(List<SkinItems> skins) {
        this.skins = skins;
    }
}
