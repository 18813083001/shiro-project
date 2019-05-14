package com.thorough.core.modules.pathology.model.dao;

import com.thorough.core.modules.pathology.model.vo.StatisticsVo;
import com.thorough.library.mybatis.persistence.annotation.MyBatisDao;
import com.thorough.library.mybatis.persistence.model.dao.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@MyBatisDao
public interface Statistics2Dao extends Dao{
    /**
     * @param hospitalId 医院ID
     * @param userIdList  userIdList=null表示统计所有用户，userIdList !=null,userIdList.size()=0时，表示不统计任何人的数据，统计结果为空
     * @param diseaseIdList diseaseIdList=null表示统计所有配置项，diseaseIdList !=null,diseaseIdList.size()=0时，表示不统计任何配置项，统计结果为空
     * */
    StatisticsVo getImageTotalAndUnAllocationImageTotal(@Param(value = "sourceHospitalIds") List<String> sourceHospitalIds,
                                                        @Param(value = "hospitalId") String hospitalId,
                                                        @Param(value = "userIdList") List<String> userIdList,
                                                        @Param(value = "diseaseIdList") List<String> diseaseIdList);

    /**
     * @param hospitalId 医院ID
     * @param userIdList  userIdList不能为空
     * @param diseaseIdList diseaseIdList=null表示统计所有配置项，diseaseIdList !=null,diseaseIdList.size()=0时，表示不统计任何配置项，统计结果为空
     * */
    List<StatisticsVo> getStatisticsDoctor(@Param(value = "sourceHospitalIds") List<String> sourceHospitalIds,
                                           @Param(value = "hospitalId") String hospitalId,
                                           @Param(value = "userIdList") List<String> userIdList,
                                           @Param(value = "diseaseIdList") List<String> diseaseIdList,
                                           @Param(value = "createDate") Date createDate,
                                           @Param(value = "createEndDate") Date createEndDate);

    /**
     * @param  reviewStage 20表示统计专家，30表示统计顾问
     * */
    List<StatisticsVo> getStatisticsExpertOrAdvisor(@Param(value = "userIds") List<String> userIds,
                                                    @Param(value = "diseaseIds") List<String> diseaseIds,
                                                    @Param(value = "reviewStage")int reviewStage,
                                                    @Param(value = "createDate") Date createDate,
                                                    @Param(value = "createEndDate") Date createEndDate);

    /**
     * @param  reviewStage 20表示统计专家，30表示统计顾问
     * */
    long getTotalNumberExpertOrAdvisorUnAllocating(@Param(value = "diseaseIdList") List<String> diseaseIdList,@Param(value = "reviewStage")int reviewStage);
}
