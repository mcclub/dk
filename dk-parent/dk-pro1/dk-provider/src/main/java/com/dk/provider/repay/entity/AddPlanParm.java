package com.dk.provider.repay.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 新增还款计划接口参数
 */
public class AddPlanParm implements Serializable {
    private Double setAmount;//预留金额
    private String startDate;//还款开始时间
    private String billDate;//还款结束时间
    private Double billAmount;//还款总额
    private Double chargeRate;//费率
    private BigDecimal paymentRate;//单笔
    private String dateListStr;//执行日期的字符串
    private Long userId;//用户id
    private String cardCode;//卡号
    private String province;//省份
    private String city;//地市
    private String routId;//大类通道id
    private String subId;//小类通道id

    public Double getSetAmount() {
        return setAmount;
    }

    public void setSetAmount(Double setAmount) {
        this.setAmount = setAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public Double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Double billAmount) {
        this.billAmount = billAmount;
    }

    public Double getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(Double chargeRate) {
        this.chargeRate = chargeRate;
    }

    public BigDecimal getPaymentRate() {
        return paymentRate;
    }

    public void setPaymentRate(BigDecimal paymentRate) {
        this.paymentRate = paymentRate;
    }

    public String getDateListStr() {
        return dateListStr;
    }

    public void setDateListStr(String dateListStr) {
        this.dateListStr = dateListStr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRoutId() {
        return routId;
    }

    public void setRoutId(String routId) {
        this.routId = routId;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }
}
