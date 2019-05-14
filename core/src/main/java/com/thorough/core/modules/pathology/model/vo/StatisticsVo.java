package com.thorough.core.modules.pathology.model.vo;


import java.io.Serializable;

public class StatisticsVo implements Serializable{

    private String userId;
    private String userName;
    private Long total = 0L;
    private Long unAllocated = 0L;
    private Long allocated = 0L;
    private Long difficult = 0L;
    private Long review10 = 0L;
    private Long review11 = 0L;

    private Long totalAllocated10 = 0L; //一审分配切片总数
    private Long totalSubmit10 = 0L;    //一审完成切片总数
    private Long totalUnSubmit10 = 0L;  //一审未完成切片总数

    private Long totalAllocated11 = 0L; //二审分配切片总数
    private Long totalSubmit11 = 0L;    //二审完成切片总数
    private Long totalUnSubmit11 = 0L;  //二审未完成切片总数

    private Long yesterdaySubmit10 = 0L; //昨天完成一审数
    private Long yesterdaySubmit11 = 0L; //昨天完成二审数

    private Long unLabel10 = 0L;   // 一审待标注
    private Long onLabel10 = 0L;   // 一审正在标注
    private Long submit10  = 0L;   // 一审已提交
    private Long unLabel11 = 0L;   // 二审待标注
    private Long onLabel11 = 0L;   // 二审正在标注
    private Long submit11 = 0L;   // 二审已提交
    private Long onLabel20 = 0L;   // 专家正在标注
    private Long unLabel20 = 0L;   // 专家待审核
    private Long onLabel30 = 0L;   // 顾问正在标注
    private Long unLabel30 = 0L;   // 顾问待标注


    /*
    * 一审分配切片总数
    * */
    private Long review10AllocatingTotal;
    /*
    * 一审完成切片总数
    * */
    private Long review10CompletedTotal;
    /*
    * 一审完未成数
    * */
    private Long review10UnCompletedTotal;
    /*
   * 二审分配切片总数
   * */
    private Long review11AllocatingTotal;
    /*
   * 二审完成切片总数
   * */
    private Long review11CompletedTotal;
    /*
   * 二审未完成数
   * */
    private Long review11UnCompletedTotal;
    /*
   * 昨天完成一审数
   * */
    private Long yesterday10CompletedTotal;
    /*
  * 昨天完成二审数
  * */
    private Long yesterday11CompletedTotal;

    /*
    * 专家分配总数
    * */
    private Long review20AllocatingTotal;
    /*
    * 专家完成数
    * */
    private Long review20CompletedTotal;
    /*
    * 专家未完成数
    * */
    private Long review20UnCompletedTotal;

    /*
    * 顾问分配总数
    * */
    private Long review30AllocatingTotal;
    /*
   * 顾问完成数
   * */
    private Long review30CompletedTotal;
    /*
   * 顾问未完成数
   * */
    private Long review30UnCompletedTotal;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getReview10AllocatingTotal() {
        return review10AllocatingTotal;
    }

    public void setReview10AllocatingTotal(Long review10AllocatingTotal) {
        this.review10AllocatingTotal = review10AllocatingTotal;
    }

    public Long getReview10CompletedTotal() {
        return review10CompletedTotal;
    }

    public void setReview10CompletedTotal(Long review10CompletedTotal) {
        this.review10CompletedTotal = review10CompletedTotal;
    }

    public Long getReview10UnCompletedTotal() {
        return review10UnCompletedTotal;
    }

    public void setReview10UnCompletedTotal(Long review10UnCompletedTotal) {
        this.review10UnCompletedTotal = review10UnCompletedTotal;
    }

    public Long getReview11AllocatingTotal() {
        return review11AllocatingTotal;
    }

    public void setReview11AllocatingTotal(Long review11AllocatingTotal) {
        this.review11AllocatingTotal = review11AllocatingTotal;
    }

    public Long getReview11CompletedTotal() {
        return review11CompletedTotal;
    }

    public void setReview11CompletedTotal(Long review11CompletedTotal) {
        this.review11CompletedTotal = review11CompletedTotal;
    }

    public Long getReview11UnCompletedTotal() {
        return review11UnCompletedTotal;
    }

    public void setReview11UnCompletedTotal(Long review11UnCompletedTotal) {
        this.review11UnCompletedTotal = review11UnCompletedTotal;
    }

    public Long getYesterday10CompletedTotal() {
        return yesterday10CompletedTotal;
    }

    public void setYesterday10CompletedTotal(Long yesterday10CompletedTotal) {
        this.yesterday10CompletedTotal = yesterday10CompletedTotal;
    }

    public Long getYesterday11CompletedTotal() {
        return yesterday11CompletedTotal;
    }

    public void setYesterday11CompletedTotal(Long yesterday11CompletedTotal) {
        this.yesterday11CompletedTotal = yesterday11CompletedTotal;
    }

    public Long getReview20AllocatingTotal() {
        return review20AllocatingTotal;
    }

    public void setReview20AllocatingTotal(Long review20AllocatingTotal) {
        this.review20AllocatingTotal = review20AllocatingTotal;
    }

    public Long getReview20CompletedTotal() {
        return review20CompletedTotal;
    }

    public void setReview20CompletedTotal(Long review20CompletedTotal) {
        this.review20CompletedTotal = review20CompletedTotal;
    }

    public Long getReview20UnCompletedTotal() {
        return review20UnCompletedTotal;
    }

    public void setReview20UnCompletedTotal(Long review20UnCompletedTotal) {
        this.review20UnCompletedTotal = review20UnCompletedTotal;
    }

    public Long getReview30AllocatingTotal() {
        return review30AllocatingTotal;
    }

    public void setReview30AllocatingTotal(Long review30AllocatingTotal) {
        this.review30AllocatingTotal = review30AllocatingTotal;
    }

    public Long getReview30CompletedTotal() {
        return review30CompletedTotal;
    }

    public void setReview30CompletedTotal(Long review30CompletedTotal) {
        this.review30CompletedTotal = review30CompletedTotal;
    }

    public Long getReview30UnCompletedTotal() {
        return review30UnCompletedTotal;
    }

    public void setReview30UnCompletedTotal(Long review30UnCompletedTotal) {
        this.review30UnCompletedTotal = review30UnCompletedTotal;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUnAllocated() {
        return unAllocated;
    }

    public void setUnAllocated(Long unAllocated) {
        this.unAllocated = unAllocated;
    }

    public Long getAllocated() {
        return allocated;
    }

    public void setAllocated(Long allocated) {
        this.allocated = allocated;
    }

    public Long getDifficult() {
        return difficult;
    }

    public void setDifficult(Long difficult) {
        this.difficult = difficult;
    }

    public Long getReview10() {
        return review10;
    }

    public void setReview10(Long review10) {
        this.review10 = review10;
    }

    public Long getReview11() {
        return review11;
    }

    public void setReview11(Long review11) {
        this.review11 = review11;
    }

    public Long getTotalAllocated10() {
        return totalAllocated10;
    }

    public void setTotalAllocated10(Long totalAllocated10) {
        this.totalAllocated10 = totalAllocated10;
    }

    public Long getTotalSubmit10() {
        return totalSubmit10;
    }

    public void setTotalSubmit10(Long totalSubmit10) {
        this.totalSubmit10 = totalSubmit10;
    }

    public Long getTotalUnSubmit10() {
        return totalUnSubmit10;
    }

    public void setTotalUnSubmit10(Long totalUnSubmit10) {
        this.totalUnSubmit10 = totalUnSubmit10;
    }

    public Long getTotalAllocated11() {
        return totalAllocated11;
    }

    public void setTotalAllocated11(Long totalAllocated11) {
        this.totalAllocated11 = totalAllocated11;
    }

    public Long getTotalSubmit11() {
        return totalSubmit11;
    }

    public void setTotalSubmit11(Long totalSubmit11) {
        this.totalSubmit11 = totalSubmit11;
    }

    public Long getTotalUnSubmit11() {
        return totalUnSubmit11;
    }

    public void setTotalUnSubmit11(Long totalUnSubmit11) {
        this.totalUnSubmit11 = totalUnSubmit11;
    }

    public Long getYesterdaySubmit10() {
        return yesterdaySubmit10;
    }

    public void setYesterdaySubmit10(Long yesterdaySubmit10) {
        this.yesterdaySubmit10 = yesterdaySubmit10;
    }

    public Long getYesterdaySubmit11() {
        return yesterdaySubmit11;
    }

    public void setYesterdaySubmit11(Long yesterdaySubmit11) {
        this.yesterdaySubmit11 = yesterdaySubmit11;
    }

    public Long getUnLabel10() {
        return unLabel10;
    }

    public void setUnLabel10(Long unLabel10) {
        this.unLabel10 = unLabel10;
    }

    public Long getOnLabel10() {
        return onLabel10;
    }

    public void setOnLabel10(Long onLabel10) {
        this.onLabel10 = onLabel10;
    }

    public Long getSubmit10() {
        return submit10;
    }

    public void setSubmit10(Long submit10) {
        this.submit10 = submit10;
    }

    public Long getUnLabel11() {
        return unLabel11;
    }

    public void setUnLabel11(Long unLabel11) {
        this.unLabel11 = unLabel11;
    }

    public Long getOnLabel11() {
        return onLabel11;
    }

    public void setOnLabel11(Long onLabel11) {
        this.onLabel11 = onLabel11;
    }

    public Long getSubmit11() {
        return submit11;
    }

    public void setSubmit11(Long submit11) {
        this.submit11 = submit11;
    }

    public Long getOnLabel20() {
        return onLabel20;
    }

    public void setOnLabel20(Long onLabel20) {
        this.onLabel20 = onLabel20;
    }

    public Long getUnLabel20() {
        return unLabel20;
    }

    public void setUnLabel20(Long unLabel20) {
        this.unLabel20 = unLabel20;
    }

    public Long getOnLabel30() {
        return onLabel30;
    }

    public void setOnLabel30(Long onLabel30) {
        this.onLabel30 = onLabel30;
    }

    public Long getUnLabel30() {
        return unLabel30;
    }

    public void setUnLabel30(Long unLabel30) {
        this.unLabel30 = unLabel30;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
