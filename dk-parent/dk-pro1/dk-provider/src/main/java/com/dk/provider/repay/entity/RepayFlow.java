package com.dk.provider.repay.entity;

/**
 * 还款交易流水表
 */
public class RepayFlow {
    private Long id;

    private Long userId;

    private String orderNo;

    private String amount;

    private String cardcodeOut;

    private String cardcodeIn;

    private String routeId;

    private String rate;

    private String fee;

    private String states;

    private String createTime;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount == null ? null : amount.trim();
    }

    public String getCardcodeOut() {
        return cardcodeOut;
    }

    public void setCardcodeOut(String cardcodeOut) {
        this.cardcodeOut = cardcodeOut == null ? null : cardcodeOut.trim();
    }

    public String getCardcodeIn() {
        return cardcodeIn;
    }

    public void setCardcodeIn(String cardcodeIn) {
        this.cardcodeIn = cardcodeIn == null ? null : cardcodeIn.trim();
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId == null ? null : routeId.trim();
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate == null ? null : rate.trim();
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee == null ? null : fee.trim();
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states == null ? null : states.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }
}