package com.dk.provider.repay.entity;

import java.io.Serializable;

/**
 * 用户费率等级
 */
public class RecordUserRate implements Serializable {

    /**
     * 用户id
     */
    private String userId;
    /**
     * 具体刷卡通道费率
     */
    private String rate;

    /**
     * 等级id
     */
    private String classId;

    /**
     * 刷卡大类通道id
     */
    private String routeId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
