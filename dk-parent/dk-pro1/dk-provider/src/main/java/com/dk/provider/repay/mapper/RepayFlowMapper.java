package com.dk.provider.repay.mapper;

import com.dk.provider.repay.entity.RepayFlow;

public interface RepayFlowMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RepayFlow record);

    int insertSelective(RepayFlow record);

    RepayFlow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepayFlow record);

    int updateByPrimaryKey(RepayFlow record);
}