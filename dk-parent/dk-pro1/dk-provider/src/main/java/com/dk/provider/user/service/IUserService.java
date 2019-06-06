package com.dk.provider.user.service;

import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.entity.User;

import java.util.List;
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
    RestResult login(Map map) throws Exception;

    /**
     * 用户id查询绑定卡信息
     * @param map  用户id
     * @param map  01储蓄卡,02信用卡
     * @param map  卡号
     * @return
     * @throws Exception
     */
    CardInfo queryCard(Map map) throws Exception;


    /**
     * 通过绑卡修改姓名，身份证
     * @param map
     * @return
     */
    int updateByBindCard (Map map);

    /**
     * 修改用户登录密码
     * @param map
     * @return
     */
    RestResult updatePassword (Map map);


    /**
     * 用户修改密码时判断输入的旧密码是否正确
     * @param map
     * @return
     */
    boolean comparePassword (Map map);

    int udpOpautopay(Map map);

    /**
     * 根据用户id查询所有上级用户
     * @param map
     * @return
     */
    List<String> findSuperior(Map map);


    /**
     * 找回密码
     * @param map
     * @return
     */
    RestResult retrievePassword(Map map);
}
