package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.ImageReviewPoolDao;
import com.thorough.core.modules.pathology.model.entity.ImageReviewPool;
import com.thorough.library.specification.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ImageReviewPoolService extends CommonService<String,ImageReviewPoolDao,ImageReviewPool> {

}
