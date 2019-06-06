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

    private String statusStr;

    private Long operatingType;

    private String operatingTypeStr;

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
        this.orderNo = orderNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public Long getOperatingType() {
        return operatingType;
    }

    public void setOperatingType(Long operatingType) {
        this.operatingType = operatingType;
    }

    public String getOperatingTypeStr() {
        return operatingTypeStr;
    }

    public void setOperatingTypeStr(String operatingTypeStr) {
        this.operatingTypeStr = operatingTypeStr;
    }

    public Long getStatusTransaction() {
        return statusTransaction;
    }

    public void setStatusTransaction(Long statusTransaction) {
        this.statusTransaction = statusTransaction;
    }

    public String getStatusTransactionStr() {
        return statusTransactionStr;
    }

    public void setStatusTransactionStr(String statusTransactionStr) {
        this.statusTransactionStr = statusTransactionStr;
    }

    private String statusTransactionStr;




}