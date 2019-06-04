package com.dk.provider.repay.service;

import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.repay.entity.AddPlanParm;
import com.dk.provider.repay.entity.RepayPlan;

import java.util.Map;

public interface RepayPlanService extends BaseServiceI<RepayPlan> {
    /**
     * 新增还款计划
     * @param parm
     * @return
     */
    RestResult addPlan (AddPlanParm parm);


    /**
     * 立即执行还款计划
     * @param map
     * @return
     */
    RestResult activePlan (Map map);
}
