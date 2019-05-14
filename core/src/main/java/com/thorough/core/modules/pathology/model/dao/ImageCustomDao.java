package com.thorough.core.modules.pathology.model.dao;


import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface ImageCustomDao extends Dao {
    List<String> selectImageIdListByHospitalIdAndDiseaseId(@Param(value = "hospitalId") String hospitalId, @Param(value = "diseaseId") String diseaseId);
    List<String> selectImageIdListByHospitalIdAndDiseaseIdFromPool(@Param(value = "hospitalId") String hospitalId, @Param(value = "diseaseId") String diseaseId, @Param(value = "reviewStage") int reviewStage, @Param(value = "userId") String userId);

}
