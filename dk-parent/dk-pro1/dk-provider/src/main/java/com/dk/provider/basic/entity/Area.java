package com.dk.provider.basic.entity;

import java.io.Serializable;

public class Area implements Serializable {
    private Long id;

    private Long parentId;

    private String areaName;

    private String areaLevel;

    private String areaFull;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName == null ? null : areaName.trim();
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel == null ? null : areaLevel.trim();
    }

    public String getAreaFull() {
        return areaFull;
    }

    public void setAreaFull(String areaFull) {
        this.areaFull = areaFull == null ? null : areaFull.trim();
    }
}