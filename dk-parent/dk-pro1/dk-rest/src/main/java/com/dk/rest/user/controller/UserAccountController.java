package com.dk.rest.user.controller;

import com.alibaba.fastjson.JSON;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.controller.BaseController;
import com.common.utils.StringUtil;
import com.dk.provider.basic.entity.OemConfig;
import com.dk.provider.basic.service.IOemConfigService;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.entity.Withdraw;
import com.dk.provider.user.service.IUserAccountService;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/userAccount")
public class UserAccountController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(UserAccountController.class);

    @Resource
    private IUserAccountService userAccountServiceImpl;
    @Resource
    private RedisCacheService redisCacheService;
    @Resource
    private IOemConfigService oemConfigServiceImpl;

    /**
     * 用户修改登录密码
     * @param map
     * @return
     */
    @RequestMapping("/updatePayPassword")
    public RestResult updatePayPassword (@RequestBody Map map) {
        logger.info("start update pay password...");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (!StringUtil.isNotEmpty(map.get("userId"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
            if (!StringUtil.isNotEmpty(map.get("phone"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"手机号不能为空");
            }
            if ((map.get("verificationCode")).equals(redisCacheService.get((String)map.get("phone")))) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码输入有误！");
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
            return userAccountServiceImpl.updatePayPassword(map);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }


    /**
     * 用户支付验证
     * @return
     */
    @RequestMapping("/payVerification")
    public RestResult payVerification (@RequestBody Map map) {
        logger.info("start payVerification ...");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (!StringUtil.isNotEmpty(map.get("userId"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
            if (!StringUtil.isNotEmpty(map.get("passWord"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"密码不能为空");
            }
            return userAccountServiceImpl.payVerification(map);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }


    /**
     * 是否设置了交易密码
     * @return
     */
    @RequestMapping("/hasSetPassword")
    public RestResult hasSetPassword (@RequestBody Map map) {
        logger.info("start hasSetPassword ...");
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (!StringUtil.isNotEmpty(map.get("userId"))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
            return userAccountServiceImpl.hasSetPassword(map);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }


    /**
     * 查询余额
     * @return
     */
    @RequestMapping("/searchBalance")
    public RestResult searchBalance (@RequestBody Map map) {
        logger.info("start searchBalance ...");
        logger.info("参数{}",map);
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            if (StringUtil.isNotEmpty(map.get("userId"))) {
                RestResult result = userAccountServiceImpl.queryByUserId(map);
                //oemId
                Long oemId = userAccountServiceImpl.searchOemIdByUserId(map);
                Map<String,Object> parm =new HashMap<>();
                parm.put("oemId",oemId);
                OemConfig oemConfig = oemConfigServiceImpl.searchByOemid(parm);
                if ((result.getRespCode()).equals("1000") && oemConfig != null) {
                    Map<String,Object> rep = new HashMap();
                    rep.put("balance",((UserAccount)result.getData()).getBalance());
                    rep.put("rate",oemConfig.getDrawRate());
                    rep.put("fee",oemConfig.getDrawFee());
                    return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",rep);
                } else {
                    return result;
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }


    @RequestMapping("/withdraw")
    public RestResult withdraw (@RequestBody Withdraw withdraw) {
        try{
            RestResult restResult = new RestResult();
            logger.info("start withdraw ...");
            logger.info("参数{}", JSON.toJSONString(withdraw));
            if (StringUtil.isNotEmpty(withdraw)) {
                if (withdraw.getAmount() == null) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"提现金额为空");
                }
                if (withdraw.getPassWord() == null) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"密码不能为空");
                }
                if (withdraw.getAmount() < 0) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"提现金额必须大于0");
                }
                if (!StringUtil.isNotEmpty(withdraw)) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
                }
                return userAccountServiceImpl.withdraw(withdraw);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
            }
        } catch (Exception e){
            logger.error("searchActivePlan"+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }
}
