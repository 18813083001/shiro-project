package com.thorough.core.modules.pathology.web;
import com.thorough.core.modules.pathology.model.vo.StatisticsVo;
import com.thorough.core.modules.pathology.service.Statistics2Service;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.service.OfficeService;
import com.thorough.library.utils.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(value = "${adminPath}/pathology/statistics/")
public class Statistics2Controller extends BaseController{

    @Autowired
    Statistics2Service statistics2Service;
    @Autowired
    OfficeService officeService;

    /**
     * 切片总数和未分配总数
     * @param hospitalId 医院id必须上传
     * @param diseaseId 可以是器官、染色、分类id，不传表示该医院所有器官
     * */
    @RequestMapping(value = "imageTotalAndUnAllocationImageTotal")
    public ResponseEntity<?> getImageTotalAndUnAllocationImageTotal(@RequestParam(required = false,defaultValue = "") List<String> sourceHospitalIds,@RequestParam String hospitalId, String diseaseId){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        StatisticsVo statisticsVo = statistics2Service.getImageTotalAndUnAllocationImageTotal(sourceHospitalIds,hospitalId,diseaseId);
        if (statisticsVo == null)
            statisticsVo = new StatisticsVo();
        Map<String,Object> map = new HashMap<>();
        map.put("hospital",statisticsVo.getUserName());
        map.put("total",statisticsVo.getTotal());
        map.put("unAllocated",statisticsVo.getUnAllocated());
        builder.add(map);
        return builder.build();
    }

    /**
     * 医生切片总数和未分配总数
     * @param hospitalId 医院id必须上传
     * @param diseaseId 可以是器官、染色、分类id，不传表示该医院所有器官
     * */
    @RequestMapping(value = "statisticsDoctorList")
    public ResponseEntity<?> getStatisticsDoctorList(@RequestParam(required = false,defaultValue = "") List<String> sourceHospitalIds,@RequestParam String hospitalId, String diseaseId,Date createDate,Date createEndDate){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        List<StatisticsVo> statisticsVoList = statistics2Service.getStatisticsDoctorList(sourceHospitalIds,hospitalId,diseaseId,createDate,createEndDate);
        if (statisticsVoList == null)
            statisticsVoList = new ArrayList<>();
        List<Map> list = new ArrayList();
        for (int i = 0;i < statisticsVoList.size();i++){
            Map<String,Object> map = new HashMap();
            StatisticsVo vo = statisticsVoList.get(i);
            map.put("userName",vo.getUserName());
            map.put("userId",vo.getUserId());
            map.put("totalAllocated10",vo.getTotalAllocated10());
            map.put("totalSubmit10",vo.getTotalSubmit10());
            map.put("totalAllocated11",vo.getTotalAllocated11());
            map.put("totalSubmit11",vo.getTotalSubmit11());
            list.add(map);
        }
        builder.add("statisticsDoctorList",list);
        return builder.build();
    }

    /**
     * 各专家切片分配总数和完成总数
     * @param diseaseId 可以是器官、染色、分类id，不传表示该医院所有器官
     * */
    @RequestMapping(value = "statisticsExpertList")
    public ResponseEntity<?> getStatisticsExpertList(String diseaseId,Date createDate,Date createEndDate){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        //专家
        List<StatisticsVo> statisticsVoList = statistics2Service.getStatisticsExpertList(diseaseId,20,createDate,createEndDate);
        if (statisticsVoList == null)
            statisticsVoList = new ArrayList<>();
        List<Map> list = new ArrayList();
        for (int i = 0;i < statisticsVoList.size();i++){
            Map<String,Object> map = new HashMap();
            StatisticsVo vo = statisticsVoList.get(i);
            map.put("userName",vo.getUserName());
            map.put("userId",vo.getUserId());
            map.put("review20AllocatingTotal",vo.getReview20AllocatingTotal());
            map.put("review20CompletedTotal",vo.getReview20CompletedTotal());
            list.add(map);
        }
        builder.add("statisticsExpertList",list);

        //顾问
        List<StatisticsVo> statisticsAdvisorVoList = statistics2Service.getStatisticsExpertList(diseaseId,30,createDate,createEndDate);
        if (statisticsAdvisorVoList == null)
            statisticsAdvisorVoList = new ArrayList<>();
        List<Map> listAdvisor = new ArrayList();
        for (int i = 0;i < statisticsAdvisorVoList.size();i++){
            Map<String,Object> map = new HashMap();
            StatisticsVo vo = statisticsAdvisorVoList.get(i);
            map.put("userName",vo.getUserName());
            map.put("userId",vo.getUserId());
            map.put("review30AllocatingTotal",vo.getReview20AllocatingTotal());
            map.put("review30CompletedTotal",vo.getReview20CompletedTotal());
            listAdvisor.add(map);
        }
        builder.add("statisticsAdvisorList",listAdvisor);
        return builder.build();
    }

    /**
     * 专家未分配切片总数
     * @param diseaseId 可以是器官、染色、分类id，不传表示该医院所有器官
     * */
    @RequestMapping(value = "totalNumberExpertUnAllocating")
    public ResponseEntity<?> getTotalNumberExpertUnAllocating(String diseaseId,@RequestParam(required = false,defaultValue = "20") int reviewStage){
        if ( reviewStage ==20 || reviewStage == 30){
            ResponseBuilder builder = ResponseBuilder.newInstance();
            long totalNumber = statistics2Service.getTotalNumberExpertUnAllocating(diseaseId,reviewStage);
            builder.add("totalNumberUnAllocating",totalNumber);
            return builder.build();
        }else {
            throw new IllegalArgumentException("reviewStage只能为20/30");
        }
    }



}
