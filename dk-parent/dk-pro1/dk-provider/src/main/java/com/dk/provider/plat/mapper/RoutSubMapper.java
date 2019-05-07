package com.dk.provider.plat.mapper;

import com.dk.provider.plat.entity.RoutSub;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoutSubMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoutSub record);

    int insertSelective(RoutSub record);

    RoutSub selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoutSub record);

    int updateByPrimaryKey(RoutSub record);
}