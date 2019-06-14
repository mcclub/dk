package com.common.Bill;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 还款详情
 */
public class PaymentDetail {
    /** 付款列表 */
    private List<ChargeDetail> chargeDetailList;
    /**  还款金额 */
    private BigDecimal paymentAmount;
    /** 费率后实际还款金额 */
    private BigDecimal paymentAmountAfterRate;
    private Date paymentDate;

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
//        return "\"bill.PaymentDetail\":{" +
//                "\"chargeDetailList\":\"" + chargeDetailList + "\"" +
//                ", \"paymentAmount\":\"" + this.getPaymentAmount() + "\"" +
//                ", \"paymentAmountAfterRate\":\"" + this.getPaymentAmountAfterRate() + "\"" +
//                '}';
        System.out.println("============charge list================");
        this.chargeDetailList.forEach(System.out::println);
        System.out.println("================================");
        return "\"bill.PaymentDetail\":{" +
                "\"paymentAmount\":\"" + this.getPaymentAmount() + "\"" +
                ", \"paymentAmountAfterRate\":\"" + this.getPaymentAmountAfterRate() + "\"" +
                ", \"chargeDetailList\":\"" + chargeDetailList + "\"" +
                ", \"paymentDate\":\"" + DateFormatUtils.format(paymentDate, "yyyy-MM-dd HH:mm:ss") + "\"" +
                '}';
    }

    public BigDecimal getPaymentAmountAfterRate() {
        if (this.paymentAmountAfterRate == null) {
            return null;
        }

        return paymentAmountAfterRate.setScale(2, BigDecimal.ROUND_CEILING);
    }

    public void setPaymentAmountAfterRate(BigDecimal paymentAmountAfterRate) {
        if (this.paymentAmountAfterRate == null) {
            this.paymentAmountAfterRate = new BigDecimal(0.00);
        }
        this.paymentAmountAfterRate = this.paymentAmountAfterRate.add(paymentAmountAfterRate);
    }

    public void setPaymentRate(BigDecimal rate) {
        this.paymentAmountAfterRate = this.paymentAmountAfterRate.subtract(rate);
    }

    public List<ChargeDetail> getChargeDetailList() {
        return chargeDetailList;
    }

    public void setChargeDetailList(List<ChargeDetail> chargeDetailList) {
        this.chargeDetailList = chargeDetailList;
    }

    public BigDecimal getPaymentAmount() {
        if (paymentAmount == null) {
            return null;
        }
        return paymentAmount.setScale(2, BigDecimal.ROUND_CEILING);
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        if (this.paymentAmount == null) {
            this.paymentAmount = new BigDecimal(0.00);
        }
        this.paymentAmount = this.paymentAmount.add(paymentAmount.setScale(2, BigDecimal.ROUND_CEILING));
    }

}
