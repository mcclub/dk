package com.dk.provider.repay.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.repay.entity.RepayFlow;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepayFlowMapper extends BaseMapper<RepayFlow> {
    int deleteByPrimaryKey(Long id);

    int insert(RepayFlow record);

    int insertSelective(RepayFlow record);

    RepayFlow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RepayFlow record);

    int updateByPrimaryKey(RepayFlow record);
}