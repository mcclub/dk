package com.dk.provider.repay.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.mapper.ReceiveRecordMapper;
import com.dk.provider.repay.service.ReceiveRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("receiveRecordService")
public class ReceiveRecordServiceImpl extends BaseServiceImpl<ReceiveRecord> implements ReceiveRecordService {

    private Logger logger = LoggerFactory.getLogger(ReceiveRecordServiceImpl.class);
    @Resource
    private ReceiveRecordMapper receiveRecordMapper;
    @Resource
    public void setSqlMapper (ReceiveRecordMapper receiveRecordMapper)
    {
        super.setBaseMapper (receiveRecordMapper);
    }


}
