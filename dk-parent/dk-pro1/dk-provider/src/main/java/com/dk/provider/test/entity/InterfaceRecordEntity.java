package com.dk.provider.test.entity;

import java.util.Date;

public class InterfaceRecordEntity {
    private Long id;

    private String interUrl;

    private String releaId;

    private String states;

    private Date creaTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterUrl() {
        return interUrl;
    }

    public void setInterUrl(String interUrl) {
        this.interUrl = interUrl == null ? null : interUrl.trim();
    }

    public String getReleaId() {
        return releaId;
    }

    public void setReleaId(String releaId) {
        this.releaId = releaId == null ? null : releaId.trim();
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states == null ? null : states.trim();
    }

    public Date getCreaTime() {
        return creaTime;
    }

    public void setCreaTime(Date creaTime) {
        this.creaTime = creaTime;
    }
}