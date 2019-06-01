package com.dk.provider.rake.entity;

import java.io.Serializable;

public class RebateSum implements Serializable {
    private double todayRebate;//当日返佣
    private double allRebate;//总返佣
    private int totalPeople;//总人数


    public double getTodayRebate() {
        return todayRebate;
    }

    public void setTodayRebate(double todayRebate) {
        this.todayRebate = todayRebate;
    }

    public double getAllRebate() {
        return allRebate;
    }

    public void setAllRebate(double allRebate) {
        this.allRebate = allRebate;
    }

    public int getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(int totalPeople) {
        this.totalPeople = totalPeople;
    }
}
