package com.dk.rest.user.controller;


import com.common.bean.PageResult;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.controller.BaseController;
import com.common.utils.EncryptionUtil;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.oem.entity.OemInfo;
import com.dk.provider.oem.service.IOemInfoService;
import com.dk.provider.user.service.IUserService;
import com.dk.rest.user.entity.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IOemInfoService oemInfoServiceImpl;
    @Resource
    private IUserService userServiceImpl;
    @Resource
    private RedisCacheService redisCacheService;




    public RestResult query(Map map) throws Exception {
        return null;
    }

    /**
     * 用户注册
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/add")
    public RestResult insert(@RequestBody User user) throws Exception {
        RestResult restResult = new RestResult();
        //参数对象是否为空
        if (StringUtil.isNotEmpty(user)) {
            //校验注册手机号
            if (!StringUtil.isMobilePhone(user.getPhone())) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"手机号码格式有误！");
                return restResult;
            }
            if (!user.getVerificationCode().equals(redisCacheService.get(user.getPhone()))) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码输入有误！");
                return restResult;
            }
            //校验密码是否为空
            if (user.getPassword() == null || user.getRepeatPassWord() == null) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"密码不能为空！");
                return restResult;
            }
            //校验两次输入的密码
            if (!user.getPassword().equals(user.getRepeatPassWord())) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"两次密码输入不一致！");
                return restResult;
            }
            //校验推荐人手机号
            if(!StringUtils.isEmpty(user.getReferPhone())){
                if (!StringUtil.isMobilePhone(user.getReferPhone())) {
                    restResult.setCodeAndMsg(ResultEnume.FAIL,"推荐人手机号码格式有误！");
                    return restResult;
                }
            }
            //判断oemid是否为空
            if (StringUtil.isNotEmpty(user.getOemId())) {
                Map<String,String> map = new HashMap();
                map.put("id",user.getOemId());
                OemInfo oemInfo = oemInfoServiceImpl.queryById(map);
                //判断oem是否存在
                if (null == oemInfo ) {
                    restResult.setCodeAndMsg(ResultEnume.FAIL,"应用标识不存在不存在！");
                    return restResult;
                } else {
                    //判断该手机号是否在该oem下注册过
                    Map<String,Object> userMap = new HashMap();
                    userMap.put("oemId",user.getOemId());
                    userMap.put("phone",user.getPhone());
                    userMap.put("states","1");
                    boolean isRegist = userServiceImpl.isPhoneRegisterOem(userMap);
                    if (!isRegist) {
                        if(!StringUtils.isEmpty(user.getReferPhone())){
                            //关联推荐人信息
                            Map<String,Object> referMap = new HashMap();
                            referMap.put("referPhone",user.getReferPhone());
                            referMap.put("oemId",user.getOemId());
                            referMap.put("states","1");
                            com.dk.provider.user.entity.User referInfo = userServiceImpl.searchReferPeople(referMap);
                            if (referInfo != null) {
                                user.setReferNo(String.valueOf(referInfo.getId()));
                            } else {
                                restResult.setCodeAndMsg(ResultEnume.FAIL,"推荐人不存在！");
                                return restResult;
                            }
                        }
                        user.setStates(1l);
                        user.setPassword(EncryptionUtil.md5(user.getPassword()));
                        user.setName(StringUtil.getRandomName());
                        user.setCreateTime(new Date());
                        user.setUpdateTime(new Date());
                        user.setIsDelete(1l);
                        user.setClassId(1l);
                        user.setClassName("青铜");
                        com.dk.provider.user.entity.User providerUser = new com.dk.provider.user.entity.User();
                        BeanUtils.copyProperties(user,providerUser);
                        return userServiceImpl.regist(restResult,providerUser);
                    } else {
                        restResult.setCodeAndMsg(ResultEnume.FAIL,"该手机号已被注册！");
                        return restResult;
                    }
                }
            }else {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"应用标识id为空！");
                return restResult;
            }
        } else {
            restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误！");
            return restResult;
        }
    }

    /**
     * 用户登录
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    public RestResult login(@RequestBody User user) throws Exception {
        RestResult restResult = new RestResult();
        //参数对象是否为空
        if (StringUtil.isNotEmpty(user)) {
            if (!StringUtil.isMobilePhone(user.getPhone())) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"手机号码格式有误！");
                return restResult;
            }
            //校验密码是否为空
            if (user.getPassword() == null) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"密码不能为空！");
                return restResult;
            }
            if (StringUtil.isEmpty(user.getOemId())) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"平台标识为空！");
                return restResult;
            }
            HashMap<String,Object> map = new HashMap<>();
            map.put("phone",user.getPhone());
            map.put("oemId",user.getOemId());
            map.put("password",user.getPassword());
            return userServiceImpl.login(map);
        } else {
            restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误！");
            return restResult;
        }
    }

    public RestResult update(User user) throws Exception {
        return null;
    }

    public RestResult delete(Long id) throws Exception {
        return null;
    }

    public PageResult page(Map map) throws Exception {
        return null;
    }


    /**
     * 用户修改登录密码
     * @param map
     * @return
     */
    @RequestMapping("/updatePassword")
    public RestResult updatePassword (@RequestBody Map map) {
        logger.info("start change password...");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (!StringUtil.isNotEmpty(map.get("userId"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
            if (!StringUtil.isNotEmpty(map.get("oldPassword"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"旧密码不能为空");
            }
            if (!StringUtil.isNotEmpty(map.get("newPassword"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"新密码不能为空");
            }
            if (!StringUtil.isNotEmpty(map.get("repeatPassword"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"重复密码不能为空");
            }
            if (!(map.get("newPassword")).equals(map.get("repeatPassword"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"两次密码输入不同");
            }
            return userServiceImpl.updatePassword(map);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }


    /**
     * 查询用户推荐的好友列表
     * @param map
     * @return
     */
    @RequestMapping("/searchFriendList")
    public RestResult searchFriendList (@RequestBody Map map) {
        logger.info("start searchFriendList...");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (StringUtil.isNotEmpty(map.get("userId"))) {
                return userServiceImpl.searchFriendList(map);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }
}
