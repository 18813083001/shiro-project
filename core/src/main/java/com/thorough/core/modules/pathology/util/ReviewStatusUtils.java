package com.thorough.core.modules.pathology.util;

import com.thorough.library.constant.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewStatusUtils {

    /**
     * 一审待标注
     */
    public static Map unLabel10(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ONE_UN);
        map.put("initialReviewStage", "10");
        map.put("reviewStage", "10");
        map.put("labelStatus", "0");
        map.put("name", "一审待标注");
        map.put("color", "#ffb87f");
        return map;
    }

    /**
     * 一审正在标注
     */
    public static Map onLabel10(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ONE_ON);
        map.put("initialReviewStage", "10");
        map.put("reviewStage", "10");
        map.put("labelStatus", "1");
        map.put("name", "一审正在标注");
        map.put("color", "#ff8b77");
        return map;
    }

    /**
     * 二审待标注
     */
    public static Map unLabel11(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_TWO_UN);
        map.put("initialReviewStage", "11");
        map.put("reviewStage", "11");
        map.put("labelStatus", "0");
        map.put("name", "二审待标注");
        map.put("color", "#eb8eba");
        return map;
    }

    /**
     * 二审正在标注
     */
    public static Map onLabel11(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_TWO_ON);
        map.put("initialReviewStage", "11");
        map.put("reviewStage", "11");
        map.put("labelStatus", "1");
        map.put("name", "二审正在标注");
        map.put("color", "#b56a9b");
        return map;
    }


    /**
     * 专家待审核
     */
    public static Map unLabel20(){
        List list = new ArrayList<>();
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_EXPERT_UN);
        map.put("initialReviewStage", "20");
        map.put("reviewStage", "20");
        map.put("labelStatus", "0");
        map.put("name", "专家待审核");
        map.put("color", "#7b7bf4");
        return map;
    }

    /**
     * 专家正在审核
     */
    public static Map onLabel20(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_EXPERT_ON);
        map.put("initialReviewStage", "20");
        map.put("reviewStage", "20");
        map.put("labelStatus", "1");
        map.put("name", "专家正在审核");
        map.put("color", "#5a45f5");
        return map;
    }

    /**
     * 顾问待审核
     */
    public static Map unLabel30(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ADVISER_UN);
        map.put("initialReviewStage", "30");
        map.put("reviewStage", "30");
        map.put("labelStatus", "0");
        map.put("name", "顾问待审核");
        map.put("color", "#83dbff");
        return map;
    }

    /**
     * 顾问正在审核
     */
    public static Map onLabel30(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ADVISER_ON);
        map.put("initialReviewStage", "30");
        map.put("reviewStage", "30");
        map.put("labelStatus", "1");
        map.put("name", "顾问正在审核");
        map.put("color", "#3dabff");
        return map;
    }

    /**
     * 一审二审状态
     * */
    public static List<Map> doctor(){
        List list = new ArrayList<>();
        list.add(unLabel10());
        list.add(onLabel10());
        list.add(unLabel11());
        list.add(onLabel11());
        return list;
    }

    /**
     * 一审已提交
     * */
    public static  Map<String,String> submit10(){
        Map<String,String> map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ONE_SUBMIT);
        map.put("initialReviewStage", "10");
        map.put("ownership", "0");
        map.put("name", "一审已提交");
        map.put("color", "#75d0ad");
        return map;
    }

    /**
     * 二审已提交
     * */
    public static  Map<String,String> submit11(){
        Map<String,String> map = new HashMap();
        map.put("spinnerId", Constant.LABEL_TWO_SUBMIT);
        map.put("initialReviewStage", "11");
        map.put("ownership", "0");
        map.put("name", "二审已提交");
        map.put("color", "#75d0ad");
        return map;
    }

    /**
     * 一审二审已提交状态
     * */
    public static List<Map<String,String>> doctorSubmit(){
        List list = new ArrayList<>();
        list.add(submit10());
        list.add(submit11());
        return list;
    }

    /**
     * 专家 前端
     * */
    public static List<Map> expert(){
        List list = new ArrayList();
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_EXPERT_UN);
        map.put("initialReviewStage", "20");
        map.put("reviewStage", "20");
        map.put("labelStatus", "0");
        map.put("name", "待审核");
        map.put("color", "#7b7bf4");
        list.add(map);
        map = new HashMap();
        map.put("spinnerId", Constant.LABEL_EXPERT_ON);
        map.put("initialReviewStage", "20");
        map.put("reviewStage", "20");
        map.put("labelStatus", "1");
        map.put("name", "正在审核");
        map.put("color", "#5a45f5");
        list.add(map);
        return list;
    }

    /**
     * 专家 管理员
     * */
    public static List<Map> expertAdmin(){
        List list = new ArrayList();
        list.add(unLabel20());
        list.add(onLabel20());
        return list;
    }


    /**
     * 顾问 前端
     * */
    public static List<Map> adviser(){
        List list = new ArrayList();
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ADVISER_UN);
        map.put("initialReviewStage", "30");
        map.put("reviewStage", "30");
        map.put("labelStatus", "0");
        map.put("name", "待审核");
        map.put("color", "#83dbff");
        list.add(map);
        map = new HashMap();
        map.put("spinnerId", Constant.LABEL_ADVISER_ON);
        map.put("initialReviewStage", "30");
        map.put("reviewStage", "30");
        map.put("labelStatus", "1");
        map.put("name", "正在审核");
        map.put("color", "#3dabff");
        list.add(map);
        return list;
    }

    /**
     * 顾问 管理员
     * */
    public static List<Map> adviserAdmin(){
        List list = new ArrayList();
        list.add(unLabel30());
        list.add(onLabel30());
        return list;
    }

    public static Map<String,String> submit20(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_20_SUBMIT);
        map.put("initialReviewStage", "20");
        map.put("ownership", "0");
        map.put("name", "专家已审核");
        map.put("color", "#83dbff");
        return map;
    }

    public static Map<String,String> submit30(){
        Map map = new HashMap();
        map.put("spinnerId", Constant.LABEL_30_SUBMIT);
        map.put("initialReviewStage", "30");
        map.put("ownership", "0");
        map.put("name", "顾问已审核");
        map.put("color", "#1c71fd");
        return map;
    }


    public static List<Map<String,String>> againAllocationSearchStatus(){
        List list = new ArrayList();
        list.add(unLabel10());
        list.add(onLabel10());
        list.add(unLabel11());
        list.add(onLabel11());
        list.add(unLabel20());
        list.add(onLabel20());
        list.add(unLabel30());
        list.add(onLabel30());
        return list;
    }

    public static List<Map<String,String>> initAllocationStatus(){
        List list = new ArrayList();
        list.add(unLabel10());
        list.add(unLabel11());
        list.add(unLabel20());
        list.add(unLabel30());
        return list;
    }

    public static List<Map<String,String>> expertAndAdvisorAllocationStatus(){
        List list = new ArrayList();
        list.addAll(expertAdmin());
        list.addAll(adviserAdmin());
        return list;
    }

    public static List<Map<String,String>> getImageInputStatus(){
        List list = new ArrayList();
        list.add(unLabel10());
        list.add(unLabel11());
        list.add(unLabel20());
        list.add(unLabel30());
        return list;
    }

    public static List<Map<String,String>> allocationReviewPoolStatus(){
        List<Map<String,String>> mapList = new ArrayList<>();
        mapList.add(ReviewStatusUtils.unLabel11());
        mapList.add(ReviewStatusUtils.unLabel20());
        mapList.add(ReviewStatusUtils.unLabel30());
        return mapList;
    }

    public static List<Map<String,String>> imageAllStatus(){
        List<Map<String,String>> mapList = new ArrayList<>();
        mapList.add(ReviewStatusUtils.unLabel10());
        mapList.add(ReviewStatusUtils.onLabel10());
        mapList.add(ReviewStatusUtils.unLabel11());
        mapList.add(ReviewStatusUtils.onLabel11());
        mapList.add(ReviewStatusUtils.unLabel20());
        mapList.add(ReviewStatusUtils.onLabel20());
        mapList.add(ReviewStatusUtils.unLabel30());
        mapList.add(ReviewStatusUtils.onLabel30());
        mapList.add(ReviewStatusUtils.submit10());
        mapList.add(ReviewStatusUtils.submit11());
        mapList.add(ReviewStatusUtils.submit20());
        mapList.add(ReviewStatusUtils.submit30());
        return mapList;
    }

    public static Map<String,String> getReviewStageMapBySpinnerId(String spinnerId,List<Map<String,String>> reviewStageList){
        for (Map<String,String> reviewStageMap:reviewStageList){
            if (reviewStageMap.get("spinnerId").equals(spinnerId))
                return reviewStageMap;
        }
        return null;
    }

    public static Map<String,String> getSpinnerIdByROIL(int reviewStage,int ownership,int initReviewStage,int labelStatus){
        if (ownership == 1){
            if (reviewStage == 10 && initReviewStage == 10 && labelStatus == 0)
                return unLabel10();
            else if (reviewStage == 10 && initReviewStage == 10 && labelStatus == 1)
                return onLabel10();
            else if (reviewStage == 11 && initReviewStage == 11 && labelStatus == 0)
                return unLabel11();
            else if (reviewStage == 11 && initReviewStage == 11 && labelStatus == 1)
                return onLabel11();
            else if (reviewStage == 20 && initReviewStage == 20 && labelStatus == 0)
                return unLabel20();
            else if (reviewStage == 20 && initReviewStage == 20 && labelStatus == 1)
                return onLabel20();
            else if (reviewStage == 30 && initReviewStage == 30 && labelStatus == 0)
                return unLabel30();
            else if (reviewStage == 30 && initReviewStage == 30 && labelStatus == 1)
                return onLabel30();
            else
                return null;
        }else if (ownership == 0){
            if (initReviewStage == 10)
                return submit10();
            else if (initReviewStage == 11)
                return submit11();
            else if (initReviewStage == 20)
                return submit20();
            else if (initReviewStage == 30)
                return submit30();
            else
                return null;
        }else
            return null;
    }

}
