package com.dk.provider.plat.entity;



public class DetailRouteInfo extends RouteInfo {
    private Long classId;
    private String className;

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
