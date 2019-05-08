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

    private String planArea;

    private Long parnId;

    private Long states;

    private Date createTime;

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
        this.cardCode = cardCode == null ? null : cardCode.trim();
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime == null ? null : billTime.trim();
    }

    public String getRepTime() {
        return repTime;
    }

    public void setRepTime(String repTime) {
        this.repTime = repTime == null ? null : repTime.trim();
    }

    public String getPlanBegin() {
        return planBegin;
    }

    public void setPlanBegin(String planBegin) {
        this.planBegin = planBegin == null ? null : planBegin.trim();
    }

    public String getPlanEnd() {
        return planEnd;
    }

    public void setPlanEnd(String planEnd) {
        this.planEnd = planEnd == null ? null : planEnd.trim();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount == null ? null : amount.trim();
    }

    public String getReseAmt() {
        return reseAmt;
    }

    public void setReseAmt(String reseAmt) {
        this.reseAmt = reseAmt == null ? null : reseAmt.trim();
    }

    public String getPlanArea() {
        return planArea;
    }

    public void setPlanArea(String planArea) {
        this.planArea = planArea == null ? null : planArea.trim();
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}