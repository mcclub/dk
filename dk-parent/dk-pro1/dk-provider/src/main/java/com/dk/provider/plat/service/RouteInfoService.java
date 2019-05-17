package com.dk.provider.plat.service;


import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.RouteUser;

import java.util.List;
import java.util.Map;

public interface RouteInfoService extends BaseServiceI<RouteInfo> {

    /**
     * 分页条件查询
     * @param map
     * @param pageable
     * @return
     * @throws Exception
     */
    Page<RouteInfo> page(Map map , Pageable pageable) throws Exception;

    RouteUser queryUserRout(Map map) throws Exception;
}
