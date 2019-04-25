package com.dk.provider.test.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.test.entity.InterfaceRecordEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterfaceRecordMapper extends BaseMapper<InterfaceRecordEntity> {
    int deleteByPrimaryKey(Long id);

    int insert(InterfaceRecordEntity record);

    int insertSelective(InterfaceRecordEntity record);

    InterfaceRecordEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InterfaceRecordEntity record);

    int updateByPrimaryKey(InterfaceRecordEntity record);
}