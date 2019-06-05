package com.dk.provider.user.service;

import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.entity.Withdraw;

import java.util.Map;

public interface IUserAccountService extends BaseServiceI<UserAccount> {
    RestResult updatePayPassword (Map map);

    RestResult payVerification (Map map);

    RestResult queryByUserId (Map map);

    RestResult hasSetPassword (Map map);

    RestResult withdraw (Withdraw withdraw);
}
