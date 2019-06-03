package com.dk.provider.classI.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.classI.entity.ClassRate;
import com.dk.provider.classI.mapper.ClassRateMapper;
import com.dk.provider.classI.service.ClassRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("classRateService")
public class ClassRateServiceImpl extends BaseServiceImpl<ClassRate> implements ClassRateService {
    private Logger logger = LoggerFactory.getLogger(ClassRateServiceImpl.class);
    @Resource
    private ClassRateMapper classRateMapper;
    @Resource
    public void setSqlMapper (ClassRateMapper classRateMapper)
    {
        super.setBaseMapper (classRateMapper);
    }

}
