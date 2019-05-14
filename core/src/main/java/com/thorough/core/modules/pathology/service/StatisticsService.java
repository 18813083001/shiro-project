package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.StatisticsDao;
import com.thorough.core.modules.pathology.model.vo.StatisticsVo;
import com.thorough.library.specification.service.BaseService;
import com.thorough.library.system.service.DiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StatisticsService implements BaseService {

    @Autowired
    StatisticsDao statisticsDao;
    @Autowired
    DiseaseService diseaseService;

    @Transactional(readOnly = false)
    public List<StatisticsVo> getStatisticsDoctorLabel(String parentId, List<String> userIdList){
        List<String> diseaseIdList = diseaseService.getDiseaseIdListByUserIdList(userIdList,"disease",parentId);
        return statisticsDao.getStatisticsDoctorLabel(userIdList,diseaseIdList);
    }

    @Transactional(readOnly = false)
    public List<StatisticsVo> getStatisticsExpertLabel(String parentId,List<String> userIdList){
        List<String> diseaseIdList = diseaseService.getDiseaseIdListByUserIdList(userIdList,"disease",parentId);
        return statisticsDao.getStatisticsExpertLabel(userIdList,diseaseIdList);

    }

    @Transactional(readOnly = false)
    public List<StatisticsVo> getStatisticsAdvisorLabel(String parentId,List<String> userIdList){
        List<String> diseaseIdList = diseaseService.getDiseaseIdListByUserIdList(userIdList,"disease",parentId);
        return statisticsDao.getStatisticsAdvisorLabel(userIdList,diseaseIdList);
    }

}
