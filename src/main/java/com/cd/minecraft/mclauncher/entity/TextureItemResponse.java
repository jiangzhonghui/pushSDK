package com.cd.minecraft.mclauncher.entity;

import com.cd.minecraft.mclauncher.dao.ScriptItems;
import com.cd.minecraft.mclauncher.dao.TextureItems;

import java.util.List;

/**
 * Created by timo on 15/3/28.
 */
public class TextureItemResponse {

    List<TextureItems> textures;

    public List<TextureItems> getTextures() {
        return textures;
    }

    public void setTextures(List<TextureItems> textures) {
        this.textures = textures;
    }
}
