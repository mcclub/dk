package com.dk.provider.user.service;

import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.user.entity.User;

import java.util.Map;

public interface IUserService extends BaseServiceI<User> {
    /**
     * 判断手机号是否在某个oem下注册过
     * @param map
     * @return
     */
    boolean isPhoneRegisterOem(Map map);

    /**
     * 查询推荐人信息
     * @param map
     * @return
     */
    User searchReferPeople(Map map);

    /**
     * 用户注册
     * @param restResult
     * @param user
     * @return
     */
    RestResult regist(RestResult restResult, User user);

    /**
     * 用户登录
     * @param map 用户登录信息
     * @return
     */
    User login(Map map);
}
