package com.dk.provider.user.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.AccountDetail;
import com.dk.provider.user.service.IAccountDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class AccountDetailServiceImpl extends BaseServiceImpl<AccountDetail> implements IAccountDetailService {
    private Logger logger = LoggerFactory.getLogger(AccountDetailServiceImpl.class);
}
