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

    /**
     * 根据用户查询代还订单汇总
     * @param map
     * @return
     */
    RestResult queryPlanByUser(Map map);



    /**
     * 查询计划汇总以及详情
     * @param id
     * @return
     */
    Map searchPlanAndDetail (Long id);
    /**
     * 修改已返金额
     * @param map
     * @return
     */
    int updreturnAmt(Map map);

    /**
     * 取消还款计划
     * @param map
     * @return
     */
    RestResult cancelPlan(Map map);
}
