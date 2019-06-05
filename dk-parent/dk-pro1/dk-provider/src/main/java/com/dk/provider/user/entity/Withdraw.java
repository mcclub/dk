package com.dk.provider.user.entity;

import java.io.Serializable;

/**
 * 用户提现接口参数
 */
public class Withdraw implements Serializable {
    private Long userId;
    private Double amount;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
