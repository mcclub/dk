package com.dk.provider.repay.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 快捷收款流水记录
 */
public class ReceiveRecord implements Serializable {
    private Long id;

    private String orderNo;

    private String userId;

    private String amount;

    private String rate;

    private String fee;

    private String receCard;

    private String settleCard;

    private String routeId;

    private String subId;

    private String province;

    private String city;

    private String industry;

    private String merchant;

    private Long states;

    private String orderDesc;

    private Date createTime;

    private Double factAmt;//到账金额

    private Double feetotal;//总手续费



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getReceCard() {
        return receCard;
    }

    public void setReceCard(String receCard) {
        this.receCard = receCard;
    }

    public String getSettleCard() {
        return settleCard;
    }

    public void setSettleCard(String settleCard) {
        this.settleCard = settleCard;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public Long getStates() {
        return states;
    }

    public void setStates(Long states) {
        this.states = states;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getFactAmt() {
        return Double.parseDouble(amount) - (Double.parseDouble(amount) * Double.parseDouble(rate) + Double.parseDouble(fee));
    }

    public void setFactAmt(Double factAmt) {
        this.factAmt = factAmt;
    }

    public Double getFeetotal() {
        return Double.parseDouble(amount) * Double.parseDouble(rate) + Double.parseDouble(fee);
    }

    public void setFeetotal(Double feetotal) {
        this.feetotal = feetotal;
    }
}