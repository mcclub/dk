package com.dk.provider.oem.service.impl;


import com.dk.provider.oem.entity.OemInfo;
import com.dk.provider.oem.mapper.OemInfoMapper;
import com.dk.provider.oem.service.IOemInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * oem业务逻辑层
 */
@Service("oemInfoServiceImpl")
public class OemInfoServiceImpl implements IOemInfoService {
    private Logger logger = LoggerFactory.getLogger(OemInfoServiceImpl.class);

    @Resource
    private OemInfoMapper oemInfoMapper;

    @Override
    public List<OemInfo> query(Map map) throws Exception {
        logger.info("first test");
        return null;
    }

    @Override
    public int insert(OemInfo oemInfo) throws Exception {
        return 0;
    }

    @Override
    public int update(OemInfo oemInfo) throws Exception {
        return 0;
    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public OemInfo queryByid(Long id) {
        return null;
    }

    @Override
    public OemInfo queryById(Map map) {
        OemInfo oemInfo = oemInfoMapper.queryById(map);
        return oemInfo;
    }
}
