package com.dk.provider.plat.service;


import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.dk.provider.plat.entity.RouteInfo;

import java.util.Map;

public interface RouteInfoService {

    /**
     * 分页条件查询
     * @param map
     * @param pageable
     * @return
     * @throws Exception
     */
    Page<RouteInfo> page(Map map , Pageable pageable) throws Exception;

}
