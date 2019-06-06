package com.dk.provider.basic.service.impl;

import com.dk.provider.basic.entity.OemConfig;
import com.dk.provider.basic.mapper.OemConfigMapper;
import com.dk.provider.basic.service.IOemConfigService;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


/**
 * oem 等级费率配置service
 */
@Service
public class OemConfigServiceImpl extends BaseServiceImpl<OemConfig> implements IOemConfigService {
    @Resource
    private OemConfigMapper oemConfigMapper;

    /**
     * 通过oemId查询费率以及单笔手续费
     * @param map
     * @return
     */
    @Override
    public OemConfig searchByOemid(Map map) {
        return oemConfigMapper.searchByOemid(map);
    }
}
