package com.dk.provider.user.service.impl;

import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.mapper.UserAccountMapper;
import com.dk.provider.user.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("userAccountServiceImpl")
public class UserAccountServiceImpl implements IUserAccountService {
    @Resource
    private UserAccountMapper userAccountMapper;

    @Override
    public List<UserAccount> query(Map map) throws Exception {
        return null;
    }

    @Override
    public int insert(UserAccount userAccount) throws Exception {
        return userAccountMapper.insert(userAccount);
    }

    @Override
    public int update(UserAccount userAccount) throws Exception {
        return 0;
    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public UserAccount queryByid(Long id) {
        return null;
    }
}
