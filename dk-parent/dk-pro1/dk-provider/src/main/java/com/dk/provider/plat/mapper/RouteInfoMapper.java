package com.dk.provider.plat.mapper;

import com.dk.provider.plat.entity.RouteInfo;

public interface RouteInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RouteInfo record);

    int insertSelective(RouteInfo record);

    RouteInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RouteInfo record);

    int updateByPrimaryKey(RouteInfo record);
}