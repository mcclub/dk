package com.dk.provider.rake.mapper;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.rake.entity.RokeCount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RokeCountMapper extends BaseMapper<RokeCount> {
    int deleteByPrimaryKey(Long id);

    int insert(RokeCount record);

    int insertSelective(RokeCount record);

    RokeCount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RokeCount record);

    int updateByPrimaryKey(RokeCount record);
}