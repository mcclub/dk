package com.dk.provider.basic.service;

import com.dk.provider.basic.entity.Area;
import com.dk.provider.basis.service.BaseServiceI;

import java.util.List;
import java.util.Map;

public interface AreaService extends BaseServiceI<Area> {
    /**
     * 查询省市两级(省份,城市)
     * @param map
     * @return
     * @throws Exception
     */
    List<Area> queryList(Map map) throws Exception;
}
