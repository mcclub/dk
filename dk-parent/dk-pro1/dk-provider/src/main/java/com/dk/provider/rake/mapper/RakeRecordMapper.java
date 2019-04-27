package com.dk.provider.rake.mapper;

import com.dk.provider.rake.entity.RakeRecord;

public interface RakeRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RakeRecord record);

    int insertSelective(RakeRecord record);

    RakeRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RakeRecord record);

    int updateByPrimaryKey(RakeRecord record);
}