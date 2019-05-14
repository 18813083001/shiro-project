package com.thorough.core.modules.pathology.imglib;


import com.thorough.core.modules.pathology.imglib.enums.ImageType;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public abstract class CacheImage{
    protected String id;
    protected long osr;
    protected String  path;
    protected String name;
    protected ImageType type;
    protected long height;
    protected long width;
    protected int  scale ;
    protected int tileSize ;
    protected int maxLevel;
    protected int minLevel;
    protected int overLap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOsr() {
        return osr;
    }

    public void setOsr(long osr) {
        this.osr = osr;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getOverLap() {
        return overLap;
    }

    public void setOverLap(int overLap) {
        this.overLap = overLap;
    }

    public abstract  BufferedImage getTile(int level, List<Integer> integers);

    public abstract BufferedImage getLabelInfo(String file) throws IOException;

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public abstract BufferedImage getThumbnailImage(int maxSize) throws IOException;
}
