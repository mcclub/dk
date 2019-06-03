package com.common.Bill;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 刷卡详情
 */
public class ChargeDetail {
    private Date chargeTime;
    /** 实际可以用来还款的金额 */
    private BigDecimal chargeAmountAfterRate;
    /** 刷卡的金额 */
    private BigDecimal chargeAmount;

    public Date getChargeTime() {
        return chargeTime;
    }


    @Override
    public String toString() {
        return "\"ChargeDetail\":{" +
                "\"chargeTime\":\"" + DateFormatUtils.format(chargeTime, "yyyy-MM-dd HH:mm:ss") + "\"" +
                ", \"chargeAmountAfterRate\":\"" + this.getChargeAmountAfterRate() + "\"" +
                ", \"chargeAmount:\"" + this.getChargeAmount() + "\"" +
                '}';
    }

    public BigDecimal getChargeAmount() {
        if (this.chargeAmount == null) {
            return null;
        }
        return chargeAmount.setScale(0, BigDecimal.ROUND_CEILING);
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }


    public void setChargeTime(Date chargeTime) {
        this.chargeTime = chargeTime;
    }

    public BigDecimal getChargeAmountAfterRate() {
        if (this.chargeAmountAfterRate == null) {
            return null;
        }
        return chargeAmountAfterRate.setScale(3, BigDecimal.ROUND_CEILING);
    }

    public void setChargeAmountAfterRate(BigDecimal chargeAmountAfterRate) {
        this.chargeAmountAfterRate = chargeAmountAfterRate;
    }

}
