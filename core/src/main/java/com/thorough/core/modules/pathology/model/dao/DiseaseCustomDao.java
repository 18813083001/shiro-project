package com.thorough.core.modules.pathology.model.dao;

import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.system.model.entity.Disease;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@MyBatisDao
public interface DiseaseCustomDao {
    List<Disease> selectImageIdListByHospitalIdAndDiseaseId(@Param(value = "diseaseId") String diseaseId);
    List<Map<String,String>> getDiseaseByDiseaseIdList(@Param("set") Collection<String> set);
}
