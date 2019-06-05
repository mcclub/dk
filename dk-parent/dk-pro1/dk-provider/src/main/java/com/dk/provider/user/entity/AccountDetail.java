package com.dk.provider.user.entity;

import java.io.Serializable;
import java.util.Date;

public class AccountDetail implements Serializable {
    private Long id;

    private Long userId;

    private String orderNo;

    private String amount;

    private Date createTime;

    private Long status;

    private Long operatingType;

    private Long statusTransaction;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount == null ? null : amount.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getOperatingType() {
        return operatingType;
    }

    public void setOperatingType(Long operatingType) {
        this.operatingType = operatingType;
    }

    public Long getStatusTransaction() {
        return statusTransaction;
    }

    public void setStatusTransaction(Long statusTransaction) {
        this.statusTransaction = statusTransaction;
    }
}