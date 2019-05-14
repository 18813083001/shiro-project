package com.thorough.core.modules.pathology.util;


import com.google.common.collect.Maps;
import com.thorough.core.modules.pathology.imglib.CacheImage;
import com.thorough.core.modules.pathology.imglib.openslide.CacheOpenSlideImage;
import com.thorough.core.modules.pathology.model.dao.ImageDao;
import com.thorough.core.modules.pathology.model.entity.Image;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CacheImageHolder implements InitializingBean {
    private  Map<String, CacheImage> cacheImageMap;
    @Autowired
    private ImageDao imageDao;



    @Override
    public void afterPropertiesSet() throws Exception {
        cacheImageMap = Maps.newConcurrentMap();
    }


    public CacheImage getCacheImage(String imageId) throws Exception{
        if (cacheImageMap.containsKey(imageId)) {
            return cacheImageMap.get(imageId);
        }
        CacheImage image = loadCacheImageByImageId(imageId);
        cacheImageMap.put(imageId,image);
        return image;

    }
    private CacheImage loadCacheImageByImageId(String imageId) throws Exception {
        Image image = imageDao.selectByPrimaryKey(imageId);
        return new CacheOpenSlideImage(image);
    }

    public CacheImage loadCacheImageByPathName(String path,String name) throws Exception {
        Image image = new Image();
        image.setPath(path);
        image.setName(name);
        return new CacheOpenSlideImage(image);
    }


}
