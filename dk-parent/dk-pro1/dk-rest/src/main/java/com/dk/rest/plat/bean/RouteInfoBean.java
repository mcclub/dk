package com.dk.rest.plat.bean;

/**
 * 前端接收返回实体类
 */
public class RouteInfoBean {

    private Long id;

    private String name;

    private String rate;

    private String fee;

    private String normMin;

    private String normMax;

    private String timeBegin;

    private String timeEnd;

    private String remark;
    /**
     * 是否自选商户(1是，0否)
     */
    private Long isFree;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getNormMin() {
        return normMin;
    }

    public void setNormMin(String normMin) {
        this.normMin = normMin;
    }

    public String getNormMax() {
        return normMax;
    }

    public void setNormMax(String normMax) {
        this.normMax = normMax;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getIsFree() {
        return isFree;
    }

    public void setIsFree(Long isFree) {
        this.isFree = isFree;
    }
}
