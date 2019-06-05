package com.dk.provider.repay.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 还款计划表
 */
public class RepayPlan implements Serializable {
    private Long id;

    private Long userId;

    private String cardCode;

    private String billTime;

    private String repTime;

    private String planBegin;

    private String planEnd;

    private String amount;

    private String reseAmt;

    private String returnAmt;

    private String planArea;

    private String planCity;

    private Long parnId;

    private Long states;

    private String statesStr;

    private Date createTime;

    private String routId;

    private String subId;

    private String notyetAmt;

    private String orderNo;

    private String returnTimes;

    private String reeTime;

    private String handlingFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public String getRepTime() {
        return repTime;
    }

    public void setRepTime(String repTime) {
        this.repTime = repTime;
    }

    public String getPlanBegin() {
        return planBegin;
    }

    public void setPlanBegin(String planBegin) {
        this.planBegin = planBegin;
    }

    public String getPlanEnd() {
        return planEnd;
    }

    public void setPlanEnd(String planEnd) {
        this.planEnd = planEnd;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReseAmt() {
        return reseAmt;
    }

    public void setReseAmt(String reseAmt) {
        this.reseAmt = reseAmt;
    }

    public String getReturnAmt() {
        return returnAmt;
    }

    public void setReturnAmt(String returnAmt) {
        this.returnAmt = returnAmt;
    }

    public String getPlanArea() {
        return planArea;
    }

    public void setPlanArea(String planArea) {
        this.planArea = planArea;
    }

    public String getPlanCity() {
        return planCity;
    }

    public void setPlanCity(String planCity) {
        this.planCity = planCity;
    }

    public Long getParnId() {
        return parnId;
    }

    public void setParnId(Long parnId) {
        this.parnId = parnId;
    }

    public Long getStates() {
        return states;
    }

    public void setStates(Long states) {
        this.states = states;
    }

    public String getStatesStr() {
        return statesStr;
    }

    public void setStatesStr(String statesStr) {
        this.statesStr = statesStr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRoutId() {
        return routId;
    }

    public void setRoutId(String routId) {
        this.routId = routId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getNotyetAmt() {
        return notyetAmt;
    }

    public void setNotyetAmt(String notyetAmt) {
        this.notyetAmt = notyetAmt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReturnTimes() {
        return returnTimes;
    }

    public void setReturnTimes(String returnTimes) {
        this.returnTimes = returnTimes;
    }

    public String getReeTime() {
        return reeTime;
    }

    public void setReeTime(String reeTime) {
        this.reeTime = reeTime;
    }

    public String getHandlingFee() {
        return handlingFee;
    }

    public void setHandlingFee(String handlingFee) {
        this.handlingFee = handlingFee;
    }
}