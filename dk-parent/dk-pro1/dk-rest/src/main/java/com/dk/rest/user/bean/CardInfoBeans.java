package com.dk.rest.user.bean;

public class CardInfoBeans {
    /**
     * 卡id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 卡号
     */
    private String cardCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 账单日
     */
    private String billTime;
    /**
     * 还款日
     */
    private String repTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public String getRepTime() {
        return repTime;
    }

    public void setRepTime(String repTime) {
        this.repTime = repTime;
    }
}
