package com.common.bean;

import java.io.Serializable;
import java.util.List;

public class RepaySplit implements Serializable {
    //每天还款金额
    private double repamt;
    //每天还款次数
    //用于还款的金额
    private int reservedAmount;
    private int repnum;
    //交易类型  1代扣，2代还
    private int type;
    //每天当中每次还款的时间
    private String time;
    //每天当中每次还款的金额
    private double amt;
    //每天当中每次还款的时间集合
    private List<String> timeList;
    //每天当中每次还款的金额集合
    private List<Double> amtList;
    //每小笔还款拆分成消费
    private List<RepaySplit> repaySplits;

    public double getRepamt() {
        return repamt;
    }

    public void setRepamt(double repamt) {
        this.repamt = repamt;
    }

    public int getReservedAmount() {
        return reservedAmount;
    }

    public void setReservedAmount(int reservedAmount) {
        this.reservedAmount = reservedAmount;
    }

    public int getRepnum() {
        return repnum;
    }

    public void setRepnum(int repnum) {
        this.repnum = repnum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public List<Double> getAmtList() {
        return amtList;
    }

    public void setAmtList(List<Double> amtList) {
        this.amtList = amtList;
    }

    public List<RepaySplit> getRepaySplits() {
        return repaySplits;
    }

    public void setRepaySplits(List<RepaySplit> repaySplits) {
        this.repaySplits = repaySplits;
    }
}
