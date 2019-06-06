package com.dk.provider.repay.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 收款纪录试图展示层实体类
 */
public class ReceiveRecordVO implements Serializable {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private Long id;

    private String orderNo;

    private String userId;

    private String amount;

    private String rate;

    private String fee;

    private String province;

    private String city;

    private String industry;

    private String merchant;

    private String states;

    private String orderDesc;

    private String createTime;

    private Double factAmt;//到账金额

    private Double feetotal;//总手续费


    public static SimpleDateFormat getSdf() {
        return sdf;
    }

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

    public String getStates() {
        return states;
    }

    public void setStates(Long states) {
        if (states == 0) {
            this.states = "处理中";
        } else if (states == 1) {
            this.states = "成功";
        }
        else if (states == 2) {
            this.states = "失败";
        }
        else if (states == 3) {
            this.states = "未知";
        }
        else if (states == 4) {
            this.states = "初始";
        }
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = sdf.format(createTime);
    }

    public Double getFactAmt() {
        return factAmt;
    }

    public void setFactAmt(Double factAmt) {
        this.factAmt = factAmt;
    }

    public Double getFeetotal() {
        return feetotal;
    }

    public void setFeetotal(Double feetotal) {
        this.feetotal = feetotal;
    }
}
