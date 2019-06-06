package com.dk.provider.basic.entity;


import java.io.Serializable;

/**
 * oem等级费率配置实体类
 */
public class OemConfig implements Serializable {
    private Long id;

    private Long oemid;

    private String drawRate;

    private String drawFee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOemid() {
        return oemid;
    }

    public void setOemid(Long oemid) {
        this.oemid = oemid;
    }

    public String getDrawRate() {
        return drawRate;
    }

    public void setDrawRate(String drawRate) {
        this.drawRate = drawRate == null ? null : drawRate.trim();
    }

    public String getDrawFee() {
        return drawFee;
    }

    public void setDrawFee(String drawFee) {
        this.drawFee = drawFee == null ? null : drawFee.trim();
    }
}