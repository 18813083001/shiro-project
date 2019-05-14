package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.ImageUserCommentDao;
import com.thorough.core.modules.pathology.model.entity.ImageUserComment;
import com.thorough.library.specification.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = false)
public class ImageUserCommentService extends CommonService<String,ImageUserCommentDao,ImageUserComment> {


}
