package com.common.Bill;

import java.math.BigDecimal;

public class BillPaymentParam {

    private String paymentDateStr;
    private BigDecimal setPerPaymentAmount;
    private BigDecimal billAmount;
    private String startDate;
    private String billDate;
    private BigDecimal chargeRate;
    private BigDecimal paymentRate;

    public BillPaymentParam(String paymentDateStr, BigDecimal setPerPaymentAmount, BigDecimal billAmount, String startDate, String billDate, BigDecimal chargeRate, BigDecimal paymentRate) {
        this.paymentDateStr = paymentDateStr;
        this.setPerPaymentAmount = setPerPaymentAmount.setScale(3, BigDecimal.ROUND_CEILING);
        this.billAmount = billAmount.setScale(3, BigDecimal.ROUND_CEILING);
        this.startDate = startDate;
        this.billDate = billDate;
        this.chargeRate = chargeRate;
        this.paymentRate = paymentRate;
    }

    public BillPaymentParam() {
    }

    @Override
    public String toString() {
        return "bill.BillPaymentParam{" +
                "paymentDateStr='" + paymentDateStr + '\'' +
                ", setPerPaymentAmount=" + setPerPaymentAmount +
                ", billAmount=" + billAmount +
                ", startDate='" + startDate + '\'' +
                ", billDate='" + billDate + '\'' +
                ", chargeRate=" + chargeRate +
                ", paymentRate=" + paymentRate +
                '}';
    }

    public String getPaymentDateStr() {
        return paymentDateStr;
    }

    public void setPaymentDateStr(String paymentDateStr) {
        this.paymentDateStr = paymentDateStr;
    }

    public BigDecimal getSetPerPaymentAmount() {
        return setPerPaymentAmount;
    }

    public void setSetPerPaymentAmount(BigDecimal setPerPaymentAmount) {
        this.setPerPaymentAmount = setPerPaymentAmount;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
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

    public BigDecimal getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(BigDecimal chargeRate) {
        this.chargeRate = chargeRate.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getPaymentRate() {
        return paymentRate;
    }

    public void setPaymentRate(BigDecimal paymentRate) {
        this.paymentRate = paymentRate.setScale(4, BigDecimal.ROUND_HALF_UP);
    }
}
