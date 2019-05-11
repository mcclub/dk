package com.dk.provider.user.service.impl;


import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.EncryptionUtil;
import com.common.utils.StringUtil;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.mapper.UserMapper;
import com.dk.provider.user.service.IUserAccountService;
import com.dk.provider.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("userServiceImpl")
public class UserServiceImpl implements IUserService {
    private Logger logger = LoggerFactory.getLogger(IUserService.class);
    @Resource
    private UserMapper userMapper;
    @Resource
    private IUserAccountService userAccountServiceImpl;
    @Resource
    private IUserService userServiceImpl;

    @Override
    public boolean isPhoneRegisterOem(Map map) {
        User user = userMapper.isPhoneRegisterOem(map);
        if (user == null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public User searchReferPeople(Map map) {
        return userMapper.searchReferPeople(map);
    }

    @Override
    @Transactional
    public RestResult regist(RestResult restResult, User user) {
        //创建用户
        try {
            int userInsertNum = userServiceImpl.insert(user);
            if (userInsertNum > 0) {
                //创建账户
                UserAccount userAccount = new UserAccount();
                userAccount.setUserId(user.getId());
                userAccount.setStates(1l);
                userAccount.setCreateTime(new Date());
                userAccount.setUpdateTime(new Date());
                userAccount.setIsDelete(1l);
                userAccount.setCreateBy(Long.toString(user.getId()));
                int accountInsertNum = userAccountServiceImpl.insert(userAccount);
                if (0 != accountInsertNum) {
                    restResult.setCodeAndMsg(ResultEnume.SUCCESS,"注册成功！");
                    return restResult;
                } else {
                    restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器异常，注册失败！");
                    return restResult;
                }

            } else {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器异常，注册失败！");
                return restResult;
            }
        } catch (Exception e) {
            restResult.setCodeAndMsg(ResultEnume.FAIL,"服务器异常，注册失败！");
            return restResult;
        }

    }

    @Override
    public RestResult login(Map map) {
        RestResult restResult = new RestResult();
        User token = userMapper.isPhoneRegisterOem(map);
        if (StringUtil.isNotEmpty(token)) {
            if (token.getPassword().equals(EncryptionUtil.md5((String)map.get("password")))) {
                if (token.getStates() == 1) {
                    token.setPassword("******");
                    restResult.setData(token);
                    restResult.setCodeAndMsg(ResultEnume.SUCCESS,"登录成功！");
                    return restResult;
                } else {
                    restResult.setCodeAndMsg(ResultEnume.FAIL,"账号已被冻结！");
                    return restResult;
                }
            } else {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"密码错误！");
                return restResult;
            }
        } else {
            restResult.setCodeAndMsg(ResultEnume.FAIL,"用户不存在！");
            return restResult;
        }
    }

    @Override
    public int updateByBindCard(Map map) {
        map.put("updateTime",new Date());
        return userMapper.updateByBindCard(map);
    }

    @Override
    public List<User> query(Map map) throws Exception {
        return null;
    }

    @Override
    public int insert(User user) throws Exception {
        int num = userMapper.insert(user);
        System.out.println(user.getId());
        return num;
    }

    @Override
    public int update(User user) throws Exception {
        return 0;
    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public User queryByid(Long id) {
        return null;
    }


}
