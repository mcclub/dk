package com.dk.provider.repay.service;

import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.repay.entity.RepayPlan;

import java.util.Map;

public interface RepayPlanService extends BaseServiceI<RepayPlan> {
    /**
     * 修改已返金额
     * @param map
     * @return
     */
    int updreturnAmt(Map map);
}
