package com.dk.provider.user.entity;

import java.io.Serializable;

/**
 * 用户推荐好友列表
 */
public class UserFriend implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 等级名称
     */
    private String className;
    /**
     * 用户头像
     */
    private String imgUrl;
    /**
     * 返佣
     */
    private double Rebate;
    /**
     * 今日返佣
     */
    private double todayRebate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getRebate() {
        return Rebate;
    }

    public void setRebate(double rebate) {
        Rebate = rebate;
    }
}
