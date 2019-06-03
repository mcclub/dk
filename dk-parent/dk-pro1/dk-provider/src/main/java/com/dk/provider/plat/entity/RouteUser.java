package com.dk.provider.plat.entity;

import java.io.Serializable;

public class RouteUser implements Serializable {

    private String userId;

    private String routId;

    private String rate;

    private String fee;

    private String settleCard;

    private String realName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoutId() {
        return routId;
    }

    public void setRoutId(String routId) {
        this.routId = routId;
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

    public String getSettleCard() {
        return settleCard;
    }

    public void setSettleCard(String settleCard) {
        this.settleCard = settleCard;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
