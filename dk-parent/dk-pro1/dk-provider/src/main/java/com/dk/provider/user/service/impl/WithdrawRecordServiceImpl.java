package com.dk.provider.user.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.WithdrawRecord;
import com.dk.provider.user.service.IWithdrawRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WithdrawRecordServiceImpl extends BaseServiceImpl<WithdrawRecord> implements IWithdrawRecordService {
    private Logger logger = LoggerFactory.getLogger(WithdrawRecordServiceImpl.class);
}
