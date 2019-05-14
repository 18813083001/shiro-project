package com.thorough.core.modules.pathology.service;

import com.thorough.core.modules.pathology.model.dao.ImageUserDao;
import com.thorough.core.modules.pathology.model.dao.Statistics2Dao;
import com.thorough.core.modules.pathology.model.entity.ImageUser;
import com.thorough.core.modules.pathology.model.vo.StatisticsVo;
import com.thorough.core.modules.sys.service.CoreDiseaseService;
import com.thorough.library.constant.Constant;
import com.thorough.library.specification.service.BaseService;
import com.thorough.library.system.service.SystemService;
import com.thorough.library.system.utils.UserUtils;
import com.thorough.library.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class Statistics2Service implements BaseService{

    @Autowired
    CoreDiseaseService coreDiseaseService;
    @Autowired
    Statistics2Dao statistics2Dao;
    @Autowired
    SystemService systemService;

    @Transactional(readOnly = false)
    public StatisticsVo getImageTotalAndUnAllocationImageTotal(List<String> sourceHospitalIds,String hospitalId, String diseaseId){
        if (StringUtils.isBlank(diseaseId))
            diseaseId = "0";
        StatisticsVo statisticsVo = null;
        if (StringUtils.isNotBlank(hospitalId)){
            List<Map<String,String>> userListByHospital = UserUtils.getUserIdNameTypeByHospitalId(hospitalId,null);
            if (userListByHospital != null && userListByHospital.size() > 0) {
                List<String> userIdList = UserUtils.getUserIdListFromUserMapList(userListByHospital);
                List<String> diseaseIdList =
                        //coreDiseaseService.getDiseaseIdListByUserIdList(userIdList, Constant.CATEGORY_DISEASE,diseaseId);
                        coreDiseaseService.getChildsByParentIdAndCategory(diseaseId,Constant.CATEGORY_DISEASE);
                //医院有用户，且医院有配置项信息才统计
                if (diseaseIdList != null)
                    statisticsVo = statistics2Dao.getImageTotalAndUnAllocationImageTotal(sourceHospitalIds,hospitalId,userIdList,diseaseIdList);
            }
        }else {
            //统计所有医院diseaseId下的数据
            List<String> diseaseIdList =
                    //coreDiseaseService.getChildsByParentIdAndCategory(diseaseId,Constant.CATEGORY_DISEASE);
                    coreDiseaseService.getChildsByParentIdAndCategory(diseaseId,Constant.CATEGORY_DISEASE);

            statisticsVo = statistics2Dao.getImageTotalAndUnAllocationImageTotal(sourceHospitalIds,null,null,diseaseIdList);
        }
        return statisticsVo;
    }


    @Transactional(readOnly = false)
    public List<StatisticsVo> getStatisticsDoctorList(List<String> sourceHospitalIds,String hospitalId, String diseaseId,Date createDate, Date createEndDate){
        if (StringUtils.isBlank(diseaseId))
            diseaseId = "0";
        List<StatisticsVo> statisticsVoList = null;
        if (StringUtils.isNoneBlank(hospitalId)){
            List<Map<String,String>> userDoctorListByHospital = UserUtils.getUserIdNameTypeByHospitalId(hospitalId,Constant.USER_DOCTOR);
            List<Map<String,String>> userListByHospital = UserUtils.getUserIdNameTypeByHospitalId(hospitalId,Constant.USER_DIRECTOR);
            if (userListByHospital == null)
                userListByHospital = new ArrayList<>();
            if (userDoctorListByHospital != null && userDoctorListByHospital.size() > 0)
                userListByHospital.addAll(userDoctorListByHospital);
            if (userListByHospital != null && userListByHospital.size() > 0) {
                List<String> userIdList = UserUtils.getUserIdListFromUserMapList(userListByHospital);
                List<String> diseaseIdList =
//                        coreDiseaseService.getDiseaseIdListByUserIdList(userIdList, Constant.CATEGORY_DISEASE,diseaseId);
                coreDiseaseService.getChildsByParentIdAndCategory(diseaseId,Constant.CATEGORY_DISEASE);
                //医院有用户，且医院有配置项信息才统计
                if (diseaseIdList != null)
                    statisticsVoList = statistics2Dao.getStatisticsDoctor(sourceHospitalIds,hospitalId,userIdList,diseaseIdList,createDate,createEndDate);
            }
        }
        return statisticsVoList;
    }

    @Transactional(readOnly = false)
    public List<StatisticsVo> getStatisticsExpertList(String diseaseId,int reviewStage,Date createDate,Date createEndDate){
        if (StringUtils.isBlank(diseaseId))
            diseaseId = "0";
        String type = Constant.EXPERT;
        if (reviewStage == 30)
            type = Constant.ADVISER;
        List<String> userIdList = systemService.getUserIdByCompanyIdAndUserType(null,type);
        List<String> diseaseIdList =
                //coreDiseaseService.getDiseaseIdListByUserIdList(userIdList,Constant.CATEGORY_DISEASE,diseaseId);
                coreDiseaseService.getChildsByParentIdAndCategory(diseaseId,Constant.CATEGORY_DISEASE);
        if (userIdList == null)
            return null;
        if (diseaseIdList == null)
            return null;
        List<StatisticsVo> statisticsExpertList = statistics2Dao.getStatisticsExpertOrAdvisor(userIdList,diseaseIdList,reviewStage,createDate,createEndDate);
        return statisticsExpertList;
    }

    @Transactional(readOnly = false)
    public long getTotalNumberExpertUnAllocating(String diseaseId,int reviewStage){
        if (StringUtils.isBlank(diseaseId))
            diseaseId = "0";
        List<String> userIdList = systemService.getUserIdByCompanyIdAndUserType(null,Constant.EXPERT);
        if (userIdList == null)
            return 0;
        List<String> diseaseIdList =
                //coreDiseaseService.getDiseaseIdListByUserIdList(userIdList,Constant.CATEGORY_DISEASE,diseaseId);
                coreDiseaseService.getChildsByParentIdAndCategory(diseaseId,Constant.CATEGORY_DISEASE);
        return statistics2Dao.getTotalNumberExpertOrAdvisorUnAllocating(diseaseIdList,reviewStage);
    }

}
