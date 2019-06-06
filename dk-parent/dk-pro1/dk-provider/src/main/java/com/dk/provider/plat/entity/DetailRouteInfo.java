package com.dk.provider.plat.entity;



public class DetailRouteInfo extends RouteInfo {
    private Long classId;
    private String className;
    private String costRate;
    private String costFee;

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

    public String getCostRate() {
        return costRate;
    }

    public void setCostRate(String costRate) {
        this.costRate = costRate;
    }

    public String getCostFee() {
        return costFee;
    }

    public void setCostFee(String costFee) {
        this.costFee = costFee;
    }
}
