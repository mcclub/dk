package com.dk.provider.user.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.Bank;
import com.dk.provider.user.mapper.BankMapper;
import com.dk.provider.user.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("bankService")
public class BankServiceImpl extends BaseServiceImpl<Bank> implements BankService {
    private Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);
    @Resource
    private BankMapper bankMapper;
    @Resource
    public void setSqlMapper (BankMapper bankMapper)
    {
        super.setBaseMapper (bankMapper);
    }

}
