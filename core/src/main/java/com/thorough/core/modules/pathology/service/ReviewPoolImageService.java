package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.ReviewPoolImageDao;
import com.thorough.core.modules.pathology.model.entity.Image;
import com.thorough.core.modules.pathology.model.entity.ImageReviewPool;
import com.thorough.library.mybatis.persistence.Page;
import com.thorough.library.specification.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewPoolImageService implements BaseService {
    @Autowired
    ReviewPoolImageDao reviewPoolImageDao;


    public List<ImageReviewPool> getReviewPoolImage(ImageReviewPool imageReviewPool){
        return reviewPoolImageDao.getReviewPoolImage(imageReviewPool);
    }

    public Page<Image> imageListPool(Image image){
        List<Image> imageList;
        imageList = reviewPoolImageDao.imageListPool(image);
        image.getPage().setList(imageList);
        return image.getPage();
    }


}
