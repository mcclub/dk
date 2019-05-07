package com.dk.provider.plat.entity;

import java.io.Serializable;

/**
 * 大类小类通道中间表
 */
public class RoutSub implements Serializable {
    private Long id;

    private String routId;

    private String subId;

    private Long states;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoutId() {
        return routId;
    }

    public void setRoutId(String routId) {
        this.routId = routId == null ? null : routId.trim();
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId == null ? null : subId.trim();
    }

    public Long getStates() {
        return states;
    }

    public void setStates(Long states) {
        this.states = states;
    }
}