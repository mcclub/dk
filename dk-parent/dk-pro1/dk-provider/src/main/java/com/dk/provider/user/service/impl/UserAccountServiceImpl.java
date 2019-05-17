package com.dk.provider.user.service.impl;

import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.mapper.UserAccountMapper;
import com.dk.provider.user.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("userAccountServiceImpl")
public class UserAccountServiceImpl extends BaseServiceImpl<UserAccount> implements IUserAccountService {
    @Resource
    private UserAccountMapper userAccountMapper;


    @Override
    public int insert(UserAccount userAccount) throws Exception {
        return userAccountMapper.insert(userAccount);
    }

}
