package com.dk.provider.plat.service.impl;

import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.mapper.RouteInfoMapper;
import com.dk.provider.plat.service.RouteInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service("routeInfoService")
public class RouteInfoServiceImpl extends BaseServiceImpl<RouteInfo> implements RouteInfoService {

    private Logger logger = LoggerFactory.getLogger(RouteInfoServiceImpl.class);
    @Resource
    private RouteInfoMapper routeInfoMapper;
    @Resource
    public void setSqlMapper (RouteInfoMapper routeInfoMapper)
    {
        super.setBaseMapper (routeInfoMapper);
    }

    /**
     * 分页条件查询
     * @param map
     * @param pageable
     * @return
     * @throws Exception
     */
    @Override
    public Page<RouteInfo> page(Map map, Pageable pageable) throws Exception {
        Page<RouteInfo> pages = super.findPages(map,pageable);
        return pages;
    }
}
