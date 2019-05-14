package com.thorough.core.modules.pathology.model.dao;

import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.ImageReviewPool;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;

import java.util.List;

@MyBatisDao
public interface ReviewPoolImageDao extends Dao {
    List<ImageReviewPool> getReviewPoolImage(ImageReviewPool imageReviewPool);
    List<Image> imageListPool(Image image);

}
