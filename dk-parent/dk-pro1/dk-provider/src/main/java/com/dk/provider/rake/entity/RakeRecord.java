package com.dk.provider.rake.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 返佣记录表
 */
public class RakeRecord implements Serializable {
    private Long id;

    private Long userId;

    private Long orderUserId;

    private String rokeAmt;

    private String orderNo;

    private Long orderType;

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

    public Long getOrderUserId() {
        return orderUserId;
    }

    public void setOrderUserId(Long orderUserId) {
        this.orderUserId = orderUserId;
    }

    public String getRokeAmt() {
        return rokeAmt;
    }

    public void setRokeAmt(String rokeAmt) {
        this.rokeAmt = rokeAmt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getOrderType() {
        return orderType;
    }

    public void setOrderType(Long orderType) {
        this.orderType = orderType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}