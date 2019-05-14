package com.thorough.core.modules.pathology.model.dao;

import com.thorough.core.modules.pathology.model.vo.StatisticsVo;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisDao
public interface StatisticsDao extends Dao {
    List<StatisticsVo> getStatisticsDoctorLabel(@Param(value = "userIds") List<String> userIds, @Param(value = "diseaseIds") List<String> diseaseIds);
    List<StatisticsVo> getStatisticsExpertLabel(@Param(value = "userIds") List<String> userIds, @Param(value = "diseaseIds") List<String> diseaseIds);
    List<StatisticsVo> getStatisticsAdvisorLabel(@Param(value = "userIds") List<String> userIds, @Param(value = "diseaseIds") List<String> diseaseIds);
}
