package com.thorough.core.modules.pathology.imglib.openslide;


import com.thorough.core.modules.pathology.exception.CoreException;
import com.thorough.core.modules.pathology.imglib.CacheImage;
import com.thorough.core.modules.pathology.imglib.enums.ImageType;
import com.thorough.core.modules.pathology.model.entity.Image;
import org.openslide.OpenSlide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CacheOpenSlideImage extends CacheImage {
    private OpenSlide openSlide;
    private Boolean isTiled = Boolean.TRUE;
    private Boolean limitBounds = Boolean.FALSE;
    private String[] BOUNDS_OFFSET_PROPS = new String[]{OpenSlide.PROPERTY_NAME_BOUNDS_X, OpenSlide.PROPERTY_NAME_BOUNDS_Y};
    private String[] BOUNDS_SIZE_PROPS = new String[]{OpenSlide.PROPERTY_NAME_BOUNDS_WIDTH, OpenSlide.PROPERTY_NAME_BOUNDS_HEIGHT};
    private List<Integer> l0Offset = new ArrayList<>();
    private List<List<Integer>> lDimensions = new ArrayList<>();
    private List<Integer> l0Dimensions = new ArrayList<>();
    private List<List<Integer>> tDimensions = new ArrayList<>();
    private Integer dzLevels;
    private List<Integer> l0ZDownSamples = new ArrayList<>();
    private List<Integer> slideFromDzLevel = new ArrayList<>();
    private List<List<Integer>> zDimensions = new ArrayList<>();
    private List<Double> lZDownSamples = new ArrayList<>();
    private List<Double> l0LDownSamples = new ArrayList<>();

    public OpenSlide getOpenSlide() {
        return openSlide;
    }

    public void setOpenSlide(OpenSlide openSlide) {
        this.openSlide = openSlide;
    }

    public CacheOpenSlideImage(Image image) throws IOException {
        this.openSlide = loadOpenSlideByImagePath(image.getPath());
        this.tileSize = openSlide.getTileSize();
        if (tileSize == -1){
            this.isTiled = Boolean.FALSE;
            tileSize = 512;
        }
        this.overLap = 0;
        this.width = openSlide.getLevel0Width();
        this.height = openSlide.getLevel0Height();
        this.maxLevel = (int) Math.ceil(Math.log(Math.max(this.height, this.width)) / Math.log(2));
        this.minLevel = this.maxLevel - (int) Math.ceil(Math.log(this.tileSize) / Math.log(2));
        this.limitBounds = true;
        this.type = ImageType.TIF;
        this.path = image.getPath();
        this.name = image.getName();

//        # We have four coordinate planes:
//        # - Row and column of the tile within the Deep Zoom level (t_)
//        # - Pixel coordinates within the Deep Zoom level (z_)
//        # - Pixel coordinates within the slide level (l_)
//        # - Pixel coordinates within slide level 0 (l0_)
//        # Precompute dimensions
//        # Slide level and offset
        Map<String, String> properties = openSlide.getProperties();
        if (limitBounds) {
            //# Level 0 coordinate offset
            for (String prop : BOUNDS_OFFSET_PROPS) {
                if (properties.containsKey(prop)) {
                    l0Offset.add(Integer.valueOf(properties.get(prop)));
                } else {
                    l0Offset.add(0);
                }
            }

            //# Slide level dimensions scale factor in each axis
            Long[] dismensions = new Long[]{openSlide.getLevelWidth(0), openSlide.getLevelHeight(0)};
            List<Long> sizeScale = new ArrayList<>();
            for (int index = 0; index < BOUNDS_SIZE_PROPS.length; index++) {
                String prop = BOUNDS_SIZE_PROPS[index];
                Long l0Lim = dismensions[index];
                if (properties.containsKey(prop)) {
                    sizeScale.add(Integer.valueOf(properties.get(prop)) / l0Lim);
                } else {
                    sizeScale.add(1L);
                }
            }


            //# Dimensions of active area
            for (int index = 0; index < openSlide.getLevelCount(); index++) {
                Long width = openSlide.getLevelWidth(index);
                Long height = openSlide.getLevelHeight(index);
                List<Integer> dimension = new ArrayList<>();
                dimension.add((int) Math.ceil((double)width * sizeScale.get(0)));
                dimension.add((int) Math.ceil((double)height * sizeScale.get(1)));
                lDimensions.add(dimension);
            }

        } else {
            for (int index = 0; index < openSlide.getLevelCount(); index++) {
                Long width = openSlide.getLevelWidth(index);
                Long height = openSlide.getLevelHeight(index);
                List<Integer> dimension = new ArrayList<>();
                dimension.add((int) Math.ceil((double)width));
                dimension.add((int) Math.ceil((double)height));
                lDimensions.add(dimension);
            }
            l0Offset = Arrays.asList(new Integer[]{0, 0});
        }


        l0Dimensions = lDimensions.get(0);
        //# Deep Zoom level
        List<Integer> zSize = l0Dimensions;
        zDimensions.addAll(lDimensions.subList(0, 1));

        while (zSize.get(0) > 1 || zSize.get(1) > 1) {
            List<Integer> newZSize = new ArrayList<>();
            for (Integer z : zSize) {
                newZSize.add(Math.max(1, (int) Math.ceil((double)z / 2)));
            }
            zSize = newZSize;
            zDimensions.add(zSize);
        }
        Collections.reverse(zDimensions);


        //# Tile
        for (List<Integer> wH : zDimensions) {
            Integer w = tiles(wH.get(0), tileSize);
            Integer h = tiles(wH.get(1), tileSize);
            tDimensions.add(Arrays.asList(w, h));
        }

        //# Deep Zoom level count
        dzLevels = zDimensions.size();

        //# Total downsamples for each Deep Zoom level
        for (Integer dzLevel = 0; dzLevel < dzLevels; dzLevel++) {
            l0ZDownSamples.add((int) Math.pow(2, dzLevels - dzLevel - 1));
        }

        // TODO : Preferred slide levels for each Deep Zoom level
        for (Integer d : l0ZDownSamples) {
            slideFromDzLevel.add(openSlide.getBestLevelForDownsample(d));
        }

        //# Piecewise downsamples
        for (int index = 0; index < openSlide.getLevelCount(); index++) {
            l0LDownSamples.add(openSlide.getLevelDownsample(index));
        }

        for (int i = 0; i < dzLevels; i++) {
            lZDownSamples.add(l0ZDownSamples.get(i) / l0LDownSamples.get(slideFromDzLevel.get(i)));
        }

        String bgColor = "#";
        if (properties.containsKey(OpenSlide.PROPERTY_NAME_BACKGROUND_COLOR)) {
            bgColor = bgColor + properties.get(OpenSlide.PROPERTY_NAME_BACKGROUND_COLOR);
        } else {
            bgColor = bgColor + "ffffff";
        }

    }

    private  OpenSlide loadOpenSlideByImagePath(String fileName) throws IOException {
        File file = new File(fileName);
        return new OpenSlide(file);
    }

    private Integer tiles(Integer zlim, int totalSize) {
        return (int) Math.ceil((double)zlim / totalSize);
    }

    public BufferedImage getTile(int level, List<Integer> address) {
        if (level < 0 || level >= dzLevels) {
            //"Invalid level"
            return null;
        }

        for (int index = 0; index < address.size(); index++) {
            if (address.get(index) < 0 || address.get(index) >= tDimensions.get(level).get(index)) {
                //"Invalid address"
                return null;
            }
        }

        Integer slideLevel = slideFromDzLevel.get(level);
        List<Integer> zOverlapTl = new ArrayList<>();
        for (int index = 0; index < address.size(); index++) {
            zOverlapTl.add(overLap * (address.get(index) != 0 ? 1 : 0));
        }

        List<Integer> zOverlapBr = new ArrayList<>();
        for (int index = 0; index < address.size(); index++) {
            Integer tlim = tDimensions.get(level).get(index);
            Integer t = address.get(index);
            zOverlapBr.add(overLap * (t!= tlim - 1 ? 1 : 0));
        }

        //# Get final size of the tile
        List<Integer> zSize = new ArrayList<>();
        for (int index = 0; index < address.size(); index++) {
            Integer t = address.get(index);
            Integer zlim = zDimensions.get(level).get(index);
            Integer ztl = zOverlapTl.get(index);
            Integer zbr = zOverlapBr.get(index);

            zSize.add(Math.min(tileSize, zlim - tileSize * t) + ztl + zbr);
        }

        List<Integer> zLocation = new ArrayList<>();
        for (int index = 0; index < address.size(); index++) {
            zLocation.add(tileSize * address.get(index));
        }

        List<Double> lLocation = new ArrayList<>();
        for (int index = 0; index < zLocation.size(); index++) {
            Integer z = zLocation.get(index);
            Integer ztl = zOverlapTl.get(index);
            lLocation.add(lZDownSamples.get(level) * (z - ztl));
        }

        List<Integer> l0Location = new ArrayList<>();
        for (int index = 0; index < lLocation.size(); index++) {
            Double l = lLocation.get(index);
            Integer l0Off = l0Offset.get(index);
            l0Location.add((l0FromL(slideLevel, l)).intValue() + l0Off);
        }

        List<Integer> lSize = new ArrayList<>();
        for (int index = 0; index < lLocation.size(); index++) {
            Double l = lLocation.get(index);
            Integer dz = zSize.get(index);
            Integer llim = lDimensions.get(slideLevel).get(index);
            lSize.add(Double.valueOf(Math.min(Math.ceil((double)lFromZ(level, dz)),
                    llim - Math.ceil((double)l))).intValue());
        }


        Integer w = lSize.get(0);
        Integer h = lSize.get(1);
        BufferedImage img = null;
//                new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        try {
            boolean raw = ((w.intValue() == tileSize) & (h.intValue() == tileSize) & (w.equals(zSize.get(0))) & (h.equals(zSize.get(1))));
            if (isTiled && raw) {
                img = readRawTile(address.get(0), address.get(1), slideLevel);
            } else {
                img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();
                int data[] = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                openSlide.paintRegionARGB(data, l0Location.get(0), l0Location.get(1), slideLevel, w, h);
                // g.scale(1.0 / relativeDS, 1.0 / relativeDS);
                g.drawImage(img, 0, 0, w, h, Color.WHITE,null);
                g.dispose();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public BufferedImage readRawTile(long tx, long ty, int level) {
        BufferedImage img = null;
        try {
            byte[] rawdata = openSlide.readRawTile(tx, ty, level);
            ByteArrayInputStream in = new ByteArrayInputStream(rawdata);
            img = ImageIO.read(in);
            return img;
        } catch (IOException e){
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public BufferedImage getLabelInfo(String file) throws IOException {
        try {
            return openSlide.getAssociatedImage("label");
        }catch (Exception e){
            try {
                return openSlide.getAssociatedImage("macro");
            }catch (Exception e1){
                throw new CoreException(file+" does not exist label and macro");
            }
        }
    }

    public BufferedImage getThumbnailImage(int maxSize) throws IOException {
        return openSlide.createThumbnailImage(maxSize);
    }

    public BufferedImage getThumbnailImage(int x,int y) throws IOException {
        return openSlide.createThumbnailImage(0,0,x,y,8000);
    }

    private Double lFromZ(int level, Integer dz) {
        return lZDownSamples.get(level) * dz;
    }


    private Double l0FromL(Integer slideLevel, Double l) {
        return l0LDownSamples.get(slideLevel) * l;
    }

    public void close(){
        openSlide.close();
    }

}
