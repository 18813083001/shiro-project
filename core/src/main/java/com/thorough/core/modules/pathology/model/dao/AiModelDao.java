package com.thorough.core.modules.pathology.model.dao;

import com.thorough.core.modules.pathology.model.entity.AiModel;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.CommonDao;

@MyBatisDao
public interface AiModelDao extends CommonDao<String,AiModel> {
}