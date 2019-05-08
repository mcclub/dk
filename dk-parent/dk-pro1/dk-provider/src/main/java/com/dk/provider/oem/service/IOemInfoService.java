package com.dk.provider.oem.service;

import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.oem.entity.OemInfo;

import java.util.Map;

public interface IOemInfoService extends BaseServiceI<OemInfo> {
    /**
     * 通过id查询
     * @param map
     * @return
     */
     OemInfo queryById(Map map);
}
