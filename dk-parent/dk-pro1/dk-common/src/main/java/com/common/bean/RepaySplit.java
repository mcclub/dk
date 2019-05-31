package com.common.bean;

import java.io.Serializable;
import java.util.List;

public class RepaySplit implements Serializable {
    //每天还款金额
    private double repamt;
    //每天还款次数
    private int repnum;
    //每天当中每次还款的时间
    private List<String> timeList;
    //每天当中每次还款的金额
    private List<String> amtList;

    public double getRepamt() {
        return repamt;
    }

    public void setRepamt(double repamt) {
        this.repamt = repamt;
    }

    public int getRepnum() {
        return repnum;
    }

    public void setRepnum(int repnum) {
        this.repnum = repnum;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public List<String> getAmtList() {
        return amtList;
    }

    public void setAmtList(List<String> amtList) {
        this.amtList = amtList;
    }
}
