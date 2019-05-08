package com.dk.provider.rake.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.rake.entity.RakeRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RakeRecordMapper extends BaseMapper<RakeRecord> {
    int deleteByPrimaryKey(Long id);

    int insert(RakeRecord record);

    int insertSelective(RakeRecord record);

    RakeRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RakeRecord record);

    int updateByPrimaryKey(RakeRecord record);
}