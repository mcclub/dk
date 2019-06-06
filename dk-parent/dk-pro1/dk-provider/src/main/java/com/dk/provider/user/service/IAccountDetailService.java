package com.dk.provider.user.service;

import com.common.bean.RestResult;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.user.entity.AccountDetail;

import java.util.Map;

public interface IAccountDetailService extends BaseServiceI<AccountDetail> {

    RestResult page(Map map, Pageable pageable);
}
