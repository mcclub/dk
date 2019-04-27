package com.dk.provider.rake.mapper;

import com.dk.provider.rake.entity.RokeCount;

public interface RokeCountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RokeCount record);

    int insertSelective(RokeCount record);

    RokeCount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RokeCount record);

    int updateByPrimaryKey(RokeCount record);
}