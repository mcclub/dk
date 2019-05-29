package com.dk.provider.repay.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.repay.entity.RepayPlan;
import com.dk.provider.repay.mapper.RepayPlanMapper;
import com.dk.provider.repay.service.RepayPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("repayPlanService")
public class RepayPlanServiceImpl extends BaseServiceImpl<RepayPlan> implements RepayPlanService {
    private Logger logger = LoggerFactory.getLogger(ReceiveRecordServiceImpl.class);

    @Resource
    private RepayPlanMapper repayPlanMapper;
    @Resource
    public void setSqlMapper (RepayPlanMapper repayPlanMapper)
    {
        super.setBaseMapper (repayPlanMapper);
    }



}
