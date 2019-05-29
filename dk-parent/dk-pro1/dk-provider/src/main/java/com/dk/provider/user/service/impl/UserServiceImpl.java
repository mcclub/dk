package com.dk.provider.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.common.utils.EncryptionUtil;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.entity.UserLogin;
import com.dk.provider.user.mapper.CardInfoMapper;
import com.dk.provider.user.mapper.UserMapper;
import com.dk.provider.user.service.IUserAccountService;
import com.dk.provider.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("userServiceImpl")
public class UserServiceImpl extends BaseServiceImpl<User> implements IUserService {
    private Logger logger = LoggerFactory.getLogger(IUserService.class);
    @Resource
    private UserMapper userMapper;
    @Resource
    private IUserAccountService userAccountServiceImpl;
    @Resource
    private IUserService userServiceImpl;

    @Resource
    private CardInfoMapper cardInfoMapper;

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
    @Transactional(rollbackFor = Exception.class)
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
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",user.getId());
                    jsonObject.put("name",user.getName());
                    restResult.setCodeAndMsg(ResultEnume.SUCCESS,"注册成功！",jsonObject);
                    return restResult;
                } else {
                    restResult.setCodeAndMsg(ResultEnume.FAIL,"关联账户创建失败！");
                    return restResult;
                }

            } else {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"用户创建失败！");
                return restResult;
            }
        } catch (Exception e) {
            restResult.setCodeAndMsg(ResultEnume.FAIL,ResultEnume.BUSYSTR);
            return restResult;
        }

    }

    @Override
    public RestResult login(Map map) throws Exception{
        RestResult restResult = new RestResult();
        User token = userMapper.isPhoneRegisterOem(map);
        if (StringUtil.isNotEmpty(token)) {
            if (token.getPassword().equals(EncryptionUtil.md5((String)map.get("password")))) {
                if (token.getStates() == 1) {
                    UserLogin userLogin = new UserLogin();
                    BeanUtils.copyProperties(token,userLogin);
                    CommonUtils.reflect(userLogin);
                    restResult.setCodeAndMsg(ResultEnume.SUCCESS,"登录成功！",userLogin);
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
    @Transactional(rollbackFor = Exception.class)
    public int updateByBindCard(Map map) {
        map.put("updateTime",new Date());
        return userMapper.updateByBindCard(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult updatePassword(Map map) {
        RestResult restResult = new RestResult();
        map.put("oldPassword",EncryptionUtil.md5((String)map.get("oldPassword")));
        boolean flag = userServiceImpl.comparePassword(map);
        if (flag) {
            map.put("updateTime",new Date());
            map.put("newPassword",EncryptionUtil.md5((String)map.get("newPassword")));
            int num = userMapper.updatePassword(map);
            if (num == 1) {
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"修改成功");
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"修改失败");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"输入的旧密码不正确");
        }
    }

    @Override
    public boolean comparePassword(Map map) {
        User user = userMapper.comparePassword(map);
        if (StringUtil.isNotEmpty(user)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int udpOpautopay(Map map) {
        return userMapper.udpOpautopay(map);
    }

    @Override
    public CardInfo queryCard(Map map) throws Exception {
        return cardInfoMapper.queryByuserId(map);
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
        return userMapper.queryByid(id);
    }


}
