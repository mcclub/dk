package com.dk.provider.plat.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.plat.entity.SmsRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsRecordMapper extends BaseMapper<SmsRecord> {
    int deleteByPrimaryKey(Long id);

    int insert(SmsRecord record);

    int insertSelective(SmsRecord record);

    SmsRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsRecord record);

    int updateByPrimaryKey(SmsRecord record);
}