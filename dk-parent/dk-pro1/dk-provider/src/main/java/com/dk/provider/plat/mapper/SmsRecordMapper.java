package com.dk.provider.plat.mapper;

import com.dk.provider.plat.entity.SmsRecord;

public interface SmsRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsRecord record);

    int insertSelective(SmsRecord record);

    SmsRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsRecord record);

    int updateByPrimaryKey(SmsRecord record);
}