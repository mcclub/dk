package com.dk.provider.user.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户信息
 */
public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String balance;

    private String rokeAmt;

    private String password;

    private Long states;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private Long isDelete;

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

    public String getBalance() {
        if (balance == null || "".equals(balance)) {
            return "0";
        }
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance == null ? null : balance.trim();
    }

    public String getRokeAmt() {
        return rokeAmt;
    }

    public void setRokeAmt(String rokeAmt) {
        this.rokeAmt = rokeAmt == null ? null : rokeAmt.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Long getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Long isDelete) {
        this.isDelete = isDelete;
    }
}