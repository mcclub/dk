package com.dk.provider.repay.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 收款历史记录查询
 */
public class ReceiveHistory implements Serializable {
    private Long id;
    private String userId;
    private String amount;
    private String receCard;
    private Long states;
    private Date createTime;
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getReceCard() {
        return receCard;
    }

    public void setReceCard(String receCard) {
        this.receCard = receCard;
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
