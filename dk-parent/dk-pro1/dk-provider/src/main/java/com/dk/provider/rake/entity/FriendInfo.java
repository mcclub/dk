package com.dk.provider.rake.entity;

import java.io.Serializable;


/**
 * 好友信息
 */
public class FriendInfo extends RakeRecord implements Serializable {
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
