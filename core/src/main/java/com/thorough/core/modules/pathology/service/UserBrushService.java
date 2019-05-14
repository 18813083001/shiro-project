package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.UserBrushDao;
import com.thorough.core.modules.pathology.model.entity.UserBrush;
import com.thorough.library.specification.service.CommonService;
import org.springframework.stereotype.Service;

@Service
public class UserBrushService extends CommonService<String,UserBrushDao,UserBrush> {
}
