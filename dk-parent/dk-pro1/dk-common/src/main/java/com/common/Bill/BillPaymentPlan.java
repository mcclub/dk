package com.common.Bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 账单还款详情
 */
public class BillPaymentPlan {
    private List<PaymentDetail> paymentDetailList = new ArrayList<>();
    private Integer paymentTimes;
    private BigDecimal billAmount;
    private BigDecimal setPerPaymentAmount;
    private List<String> paymentDates;
    private BigDecimal paymentAmount;
    private BigDecimal chargeAmount;
    private List<ChargeDetail> chargeDetails;

    @Override
    public String toString() {
        return "BillPaymentPlan{" +
//                "paymentDetailList=" + paymentDetailList +
                ", paymentTimes=" + paymentTimes +
                ", billAmount=" + billAmount +
                ", setPerPaymentAmount=" + setPerPaymentAmount +
                ", paymentDates=" + paymentDates +
                ", paymentAmount=" + paymentAmount +
                ", chargeAmount=" + chargeAmount +
                '}';
    }

    public List<ChargeDetail> getChargeDetails() {
        if (chargeDetails == null) {
            this.chargeDetails = new ArrayList<>();
        }
        return chargeDetails;
    }

    public void setChargeDetails(List<ChargeDetail> chargeDetails) {
        this.chargeDetails = chargeDetails;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount.setScale(2, BigDecimal.ROUND_CEILING);
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        if (this.chargeAmount == null) {
            this.chargeAmount = BigDecimal.ZERO;
        }
        this.chargeAmount = this.chargeAmount.add(chargeAmount);
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        if (this.paymentAmount == null) {
            this.paymentAmount = BigDecimal.ZERO;
        }
        this.paymentAmount = this.paymentAmount.add(paymentAmount);
    }

    public List<PaymentDetail> getPaymentDetailList() {
        return paymentDetailList;
    }

    public void setPaymentDetailList(List<PaymentDetail> paymentDetailList) {
        this.paymentDetailList = paymentDetailList;
    }

    public Integer getPaymentTimes() {
        return paymentTimes;
    }

    public void setPaymentTimes(Integer paymentTimes) {
        this.paymentTimes = paymentTimes;
    }

    public BigDecimal getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount) {
        this.billAmount = billAmount;
    }

    public BigDecimal getSetPerPaymentAmount() {
        return setPerPaymentAmount;
    }

    public void setSetPerPaymentAmount(BigDecimal setPerPaymentAmount) {
        this.setPerPaymentAmount = setPerPaymentAmount;
    }

    public List<String> getPaymentDates() {
        return paymentDates;
    }

    public void setPaymentDates(List<String> paymentDates) {
        this.paymentDates = paymentDates;
    }

}
