package com.dk.provider.basic.service;

import com.dk.provider.basic.entity.OemConfig;
import com.dk.provider.basis.service.BaseServiceI;

import java.util.Map;

public interface IOemConfigService extends BaseServiceI<OemConfig> {
    /**
     * 根据oemid查询用户的费率以及单笔
     * @param map
     * @return
     */
    OemConfig searchByOemid(Map map);
}
