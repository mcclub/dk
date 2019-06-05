package com.dk.rest.repay.bean;

import org.springframework.util.StringUtils;

public class RepayBindBean {
    /**
     * 卡号
     */
    private String cardCode ;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 还款总额
     */
    private String totalAmt;
    /**
     * 已还额度
     */
    private String returnAmt;
    /**
     * 未还额度
     */
    private String notyetAmt;

    /**
     * 账单日
     */
    private String billTime;

    /**
     * 还款日
     */
    private String repTime;

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTotalAmt() {
        if(StringUtils.isEmpty(totalAmt)){
            return "0";
        }else{
            return totalAmt;
        }
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getReturnAmt() {
        if(StringUtils.isEmpty(returnAmt)){
            return "0";
        }else{
            return returnAmt;
        }
    }

    public void setReturnAmt(String returnAmt) {
        this.returnAmt = returnAmt;
    }

    public String getNotyetAmt() {
        if(StringUtils.isEmpty(notyetAmt)){
            return "0";
        }else{
            return notyetAmt;
        }

    }

    public void setNotyetAmt(String notyetAmt) {
        this.notyetAmt = notyetAmt;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBillTime() {
        if(StringUtils.isEmpty(billTime)){
            return "";
        }else{
            return billTime;
        }
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public String getRepTime() {
        if(StringUtils.isEmpty(repTime)){
            return "";
        }else{
            return repTime;
        }
    }

    public void setRepTime(String repTime) {
        this.repTime = repTime;
    }
}
