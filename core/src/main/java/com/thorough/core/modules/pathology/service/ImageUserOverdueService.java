package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.ImageUserOverdueDao;
import com.thorough.core.modules.pathology.model.entity.ImageUserOverdue;
import com.thorough.library.specification.service.CommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ImageUserOverdueService  extends CommonService<String,ImageUserOverdueDao,ImageUserOverdue> {

}
