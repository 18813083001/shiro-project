package com.thorough.core.modules.pathology.imglib.kfb;

import com.thorough.core.modules.pathology.imglib.CacheImage;
import com.thorough.core.modules.pathology.imglib.enums.ImageType;
import com.thorough.core.modules.pathology.imglib.kfb.jni.KfbImageJNI;
import com.thorough.core.modules.pathology.model.entity.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class KfbImage extends CacheImage {
    protected static Logger logger = LoggerFactory.getLogger(KfbImage.class);

    public KfbImage() {
        super();
    }

    public KfbImage(Image image) throws Exception {
        this.id = image.getId();
        this.path = image.getPath();
        this.name = image.getName();
        String[] headerInfo =
//                {"31126","64061","256","40"};
                KfbImageJNI.getHeaderInfo(path);
        this.height = Integer.parseInt(headerInfo[0]);
        this.width = Integer.parseInt(headerInfo[1]);
        this.tileSize = Integer.parseInt(headerInfo[2]);
        this.scale = Integer.parseInt(headerInfo[3]);
        this.maxLevel = (int) Math.ceil(Math.log(Math.max(this.height, this.width)) / Math.log(2));
        this.minLevel = (int) Math.ceil(Math.log(this.tileSize) / Math.log(2));
        this.type = ImageType.KFB;
    }


    @Override
    public BufferedImage getTile(int level, List<Integer> address) {
        int x = address.get(0);
        int y = address.get(1);


        if (level < 1) {
            level = 1;
        }

        if (level > maxLevel) {
            level = maxLevel;
        }

        float ratio = maxLevel - level;
        int posX = x * tileSize;
        int posY = y * tileSize;
        float fscale = (float) (scale / Math.pow(2, ratio));
        String imageFile = path;
        BufferedImage image = null;

        logger.debug("get kfb image tiles: [image={}, fscale={}, posX={},posY={}]", imageFile, fscale, posX, posY);
        try {
            byte[] bytes = KfbImageJNI.getImageStreamData(imageFile, fscale, posX, posY);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);    //将b作为输入流；
                //将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
            image = ImageIO.read(in);

        } catch (IOException e) {
            logger.error("Error occur when get kfb tile:", e);
        }
        return image;
    }


    public BufferedImage getLabelInfo(String file) {
        String imageFile = file;
        BufferedImage image = null;
        try {
            byte[] bytes = KfbImageJNI.getLabelInfo(imageFile);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);    //将b作为输入流；
            //将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
            image = ImageIO.read(in);
        } catch (IOException e) {
            logger.error("Error occur when get kfb tile:", e);
        }
        return image;
    }

    public BufferedImage getThumbnailImageByMaxSize(int level) throws IOException {
        if (level < 1) {
            level = 1;
        }

        if (level > maxLevel) {
            level = maxLevel;
        }
        float ratio = maxLevel - level;
        float fscale = (float) (scale / Math.pow(2, ratio));
        int w = (int) Math.ceil((double) this.width / Math.pow(2, ratio));
        int h = (int) Math.ceil((double) this.height / Math.pow(2, ratio));
        //宽度上的块数
        int wblock = (int) Math.ceil((double)w / tileSize);
        //高度上的块数
        int hblock = (int) Math.ceil((double)h / tileSize);
        String imageFile = path;

        List<byte[]> byteslist = new ArrayList<>();
        for(int i=0;i < wblock;i ++){
            for(int j=0;j < hblock;j++){
                int x = i * tileSize;
                int y = j * tileSize;
                byte[] bytes = KfbImageJNI.getImageStreamData(imageFile, fscale, x, y);
                byteslist.add(bytes);
            }
        }

        BufferedImage ImageNew = merge(byteslist,"jpeg","",wblock,hblock);
        return ImageNew;
    }

    @Override
    public BufferedImage getThumbnailImage(int maxSize) throws IOException {

        long biger = this.height > this.width?this.height : this.width;
        long max = 0 ;
        if (maxSize < 0)
            max = 1;
        else if(maxSize > biger)
            max = biger;
        else max = maxSize;

        //缩放倍数
        long rate = 0;
        rate = (biger / max);

        //求2的对数，为了得到一个比较接近于2的倍数的放大倍数 比如rate是30，得到的实际放大倍数是16
        int ratio = (int) (Math.log((double) rate)/ Math.log((double) 2));
        float fscale = (float) (scale / Math.pow(2, ratio));
        int w = (int) Math.ceil((double) this.width / Math.pow(2, ratio));
        int h = (int) Math.ceil((double) this.height / Math.pow(2, ratio));
        //宽度上的块数
        int wblock = (int) Math.ceil((double)w / tileSize);
        //高度上的块数
        int hblock = (int) Math.ceil((double)h / tileSize);
        String imageFile = path;

        List<byte[]> byteslist = new ArrayList<>();
        for(int i=0;i < wblock;i ++){
            for(int j=0;j < hblock;j++){
                int x = i * tileSize;
                int y = j * tileSize;
                byte[] bytes = KfbImageJNI.getImageStreamData(imageFile, fscale, x, y);
                byteslist.add(bytes);
            }
        }

        BufferedImage ImageNew = merge(byteslist,"jpeg","",wblock,hblock);
        return ImageNew;
    }


    /**
     * Java拼接多张图片
     *
     * @param imgs
     * @param type
     * @param dst_pic
     * @return
     */
    public static BufferedImage merge(List<byte[]> imgs, String type, String dst_pic,int wblock,int hblock) {
        //获取需要拼接的图片长度
        int len = imgs.size();
        //判断长度是否大于0
        if (len < 1) {
            return null;
        }
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
//                src[i] = new File(imgs[i]);
//                ImageIO.read(src[i]);
                  ByteArrayInputStream in = new ByteArrayInputStream(imgs.get(i));
                  images[i] = ImageIO.read(in);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("kfb byte[]数组转images时：",e);
                return null;
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            // 从图片中读取RGB 像素
            ImageArrays[i] = new int[width * height];
            /*
            * startx images的x坐标
            * starty images的y坐标
            * w 读取的宽度
            * h 读取的高度
            * rgbArray 将读取的数据放到这里面
            * scansize 扫描的宽度
            * */
            ImageArrays[i] = images[i].getRGB(0, 0, width, height,  ImageArrays[i], 0, width);
        }

        int dst_height = 0;
        int dst_width = 0;
        //合成图片像素
        for (int i = 0; i < images.length; i++) {
            //因为合并图的时候是纵向合并，一列一列的合并，所以计算宽度的时候跳过一列的长度
            if(i%hblock==0){
                dst_width += images[i].getWidth();
            }

        }
        for(int i = 0;i < hblock;i++){
            //高度 计算一列的长度
            dst_height += images[i].getHeight();
        }
        //合成后的图片
        if (dst_height < 1) {
            logger.info("dst_height < 1");
            System.out.println("dst_height < 1");
            return null;
        }
        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(dst_width, dst_height,
                    BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int weight_i = 0;
            int k = 0;
            for (int i = 0; i < wblock; i++) {
                for(int j = 0;j < hblock;j++){
                    /*
                    * weight_i 要填充区域的x起点
                    * height_i 要填充区域的y起点
                    * w 要填充区域的宽度
                    * h 要填充区域的高度
                    * ImageArrays[k] 填充区域像素点的来源
                    * offset 读取ImageArrays[k]的偏移位置
                    * scansize 扫描填充区域的宽度
                    *
                    * */
                    ImageNew.setRGB(weight_i, height_i, images[k].getWidth(), images[k].getHeight(),
                            ImageArrays[k], 0, images[k].getWidth());
                    height_i += images[k].getHeight();
                    k++;
                }
                weight_i += images[k-1].getWidth();
                height_i = 0;
            }
            return ImageNew;
//            File outFile = new File(dst_pic);
//            ImageIO.write(ImageNew, type, outFile);// 写图片 ，输出到硬盘
        } catch (Exception e) {
            logger.error("合并kfb图片时：",e);
            return null;
        }
    }


}
