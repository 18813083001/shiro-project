package com.thorough.core.modules.pathology.model.dao;


import com.thorough.core.modules.pathology.model.entity.Label;
import com.thorough.core.modules.pathology.model.entity.LabelRelative;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisDao
public interface LabelRelativeDao extends Dao {

    List<LabelRelative> getLabels(Map param);
    String getImageId(@Param(value = "labelId") String labelId);
    int batchInsertLabel(@Param(value = "labelList")List<Label> labelList);

}
