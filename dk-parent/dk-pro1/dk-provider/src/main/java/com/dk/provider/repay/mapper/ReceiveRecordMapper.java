package com.dk.provider.repay.mapper;

import com.dk.provider.repay.entity.ReceiveRecord;

public interface ReceiveRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReceiveRecord record);

    int insertSelective(ReceiveRecord record);

    ReceiveRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReceiveRecord record);

    int updateByPrimaryKey(ReceiveRecord record);
}