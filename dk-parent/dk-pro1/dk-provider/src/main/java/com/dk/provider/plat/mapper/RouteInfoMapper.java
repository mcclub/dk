package com.dk.provider.plat.mapper;

import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.plat.entity.DetailRouteInfo;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.UserRouteinfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface RouteInfoMapper extends BaseMapper<RouteInfo> {
    int deleteByPrimaryKey(Long id);

    int insert(RouteInfo record);

    int insertSelective(RouteInfo record);

    RouteInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RouteInfo record);

    int updateByPrimaryKey(RouteInfo record);

    Page<RouteInfo> page(Map map , Pageable pageable) ;

    List<DetailRouteInfo> routeInfoByUser (Map map);

    List<DetailRouteInfo> parentRouteInfo (Map map);

    int parentRouteCount (Map map);

    int routeInfoByUserCount (Map map);
}