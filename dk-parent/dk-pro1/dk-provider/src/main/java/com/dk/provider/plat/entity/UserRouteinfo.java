package com.dk.provider.plat.entity;

import java.io.Serializable;

/**
 * 用户通道信息
 */
public class UserRouteinfo implements Serializable {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 等级id
     */
    private Long classId;
    /**
     * 等级名称
     */
    private String className;
    /**
     * 费率
     */
    private Double rate;
    /**
     * 单笔手续费
     */
    private Double fee;
    /**
     * 通道名称
     */
    private String routeName;
    /**
     * 最小交易额
     */
    private Double normMin;
    /**
     * 最大交易额
     */
    private Double normMax;
    /**
     * 交易开始时间
     */
    private String timeBegin;
    /**
     * 交易结束时间
     */
    private String timeEnd;
    /**
     * 备注
     */
    private String remark;




    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Double getNormMin() {
        return normMin;
    }

    public void setNormMin(Double normMin) {
        this.normMin = normMin;
    }

    public Double getNormMax() {
        return normMax;
    }

    public void setNormMax(Double normMax) {
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
}
