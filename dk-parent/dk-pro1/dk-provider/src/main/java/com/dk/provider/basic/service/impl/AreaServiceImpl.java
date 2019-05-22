package com.dk.provider.basic.service.impl;

import com.dk.provider.basic.entity.Area;
import com.dk.provider.basic.mapper.AreaMapper;
import com.dk.provider.basic.service.AreaService;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("areaService")
public class AreaServiceImpl extends BaseServiceImpl<Area> implements AreaService {
    private Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);
    @Resource
    private AreaMapper areaMapper;
    @Resource
    public void setSqlMapper (AreaMapper areaMapper)
    {
        super.setBaseMapper (areaMapper);
    }


    /**
     * 查询所有省份和市
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<Area> queryList(Map map) throws Exception {
        List<Area> list = areaMapper.queryList(map);
        return list;
    }


}
