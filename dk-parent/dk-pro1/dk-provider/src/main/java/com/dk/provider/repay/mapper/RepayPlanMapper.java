package com.dk.provider.repay.mapper;

import com.dk.provider.repay.entity.RepayPlan;

public interface RepayPlanMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepayPlan record);

    int insertSelective(RepayPlan record);

    RepayPlan selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepayPlan record);

    int updateByPrimaryKey(RepayPlan record);
}