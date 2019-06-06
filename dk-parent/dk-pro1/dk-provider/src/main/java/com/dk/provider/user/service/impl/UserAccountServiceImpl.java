package com.dk.provider.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.common.utils.EncryptionUtil;
import com.common.utils.StringUtil;
import com.dk.provider.basic.entity.OemConfig;
import com.dk.provider.basic.service.IOemConfigService;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.entity.Withdraw;
import com.dk.provider.user.entity.WithdrawRecord;
import com.dk.provider.user.mapper.UserAccountMapper;
import com.dk.provider.user.mapper.WithdrawRecordMapper;
import com.dk.provider.user.service.IUserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@EnableAsync
@Service("userAccountServiceImpl")
public class UserAccountServiceImpl extends BaseServiceImpl<UserAccount> implements IUserAccountService {
    private Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);
    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private WithdrawRecordMapper withdrawRecordMapper;

    @Resource
    private IOemConfigService oemConfigServiceImpl;


    @Override
    public int insert(UserAccount userAccount) throws Exception {
        return userAccountMapper.insert(userAccount);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult updatePayPassword(Map map) {
        RestResult restResult = new RestResult();
        map.put("newPassword", EncryptionUtil.md5((String)map.get("newPassword")));
        map.put("updateTime",new Date());
        int num = userAccountMapper.updatePayPassword(map);
        if (num == 1) {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"修改成功");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"修改失败");
        }
    }


    /**
     * 用户支付验证
     * @param map
     * @return
     */
    @Override
    public RestResult payVerification(Map map) {
        RestResult restResult = new RestResult();
        map.put("passWord",EncryptionUtil.md5((String)map.get("passWord")));
        UserAccount userAccount = userAccountMapper.payVerification(map);
        if (StringUtil.isNotEmpty(userAccount)) {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"验证成功");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"验证失败");
        }
    }


    /**
     * 通过用户id查询账户
     * @param map
     * @return
     */
    @Override
    public RestResult queryByUserId(Map map) {
        RestResult restResult = new RestResult();
        UserAccount userAccount = userAccountMapper.queryByUserId(map);
        if (StringUtil.isNotEmpty(userAccount)) {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",userAccount);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"该用户没有账户");
        }
    }


    /**
     * 判断账户是否设置了密码
     * @param map
     * @return
     */
    @Override
    public RestResult hasSetPassword(Map map) {
        RestResult result = this.queryByUserId(map);
        if (result.getRespCode().equals("1000")) {
            JSONObject json = new JSONObject();
            UserAccount userAccount = (UserAccount) result.getData();
            if (!StringUtil.isEmpty(userAccount.getPassword())) {
                json.put("ispass","true");
                result.setData(json);
                return result;
            } else {
                json.put("ispass","false");
                result.setData(json);
                return result;
            }
        } else {
            return result;
        }
    }

    /**
     * 用户提现
     * @param withdraw
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult withdraw(Withdraw withdraw) {
        RestResult restResult = new RestResult();
        Map<String,Object> map = new HashMap<>();
        String orderNo = CommonUtils.getOrderNo(3l);
        map.put("userId",withdraw.getUserId());
        //查询用户账户余额
        UserAccount userAccount = userAccountMapper.queryByUserId(map);
        if (StringUtil.isNotEmpty(userAccount)) {
            Long oemId = this.searchOemIdByUserId(map);
            //查询费率，等级
            Map parm = new HashMap();
            parm.put("oemId",oemId);
            OemConfig oemConfig = oemConfigServiceImpl.searchByOemid(parm);
            //如果未设置交易密码则提示设置交易密码
            if (StringUtil.isEmpty(userAccount.getPassword())) {
                return restResult.setCodeAndMsg(ResultEnume.PROCESSFAIL,"请先设置交易密码");
            }
            if (!(userAccount.getPassword()).equals(EncryptionUtil.md5(withdraw.getPassWord()))) {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"密码错误");
            }
            //如果提现金额大于账户余额
            if (withdraw.getAmount() < Double.parseDouble(userAccount.getBalance())) {
                int num = userAccountMapper.deductingBalance(withdraw);
                if (num > 0) {
                    WithdrawRecord withdrawRecord = new WithdrawRecord();
                    withdrawRecord.setAmount(String.valueOf(withdraw.getAmount()));
                    withdrawRecord.setOrderNo(orderNo);
                    withdrawRecord.setUserId(withdraw.getUserId());
                    withdrawRecord.setRate(oemConfig.getDrawRate());
                    withdrawRecord.setHandlingFee(String.valueOf(Double.valueOf(oemConfig.getDrawRate()) * withdraw.getAmount()));
                    withdrawRecord.setCreateTime(new Date());
                    withdrawRecord.setStatus(2l);
                    int insertWithdrawNum = withdrawRecordMapper.insert(withdrawRecord);
                    if (insertWithdrawNum > 0) {
                        this.moneyTransfer();
                        return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"提现功能暂未开放");
                    } else {
                        return restResult.setCodeAndMsg(ResultEnume.BUSY,ResultEnume.BUSYSTR);
                    }
                } else {
                    return restResult.setCodeAndMsg(ResultEnume.BUSY,ResultEnume.BUSYSTR);
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"余额不足");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"账户不存在，提现失败");
        }
    }


    /**
     * 通过用户id查询oemid
     * @param map
     * @return
     */
    @Override
    public Long searchOemIdByUserId(Map map) {
        return userAccountMapper.searchOemIdByUserId(map);
    }


    /**
     * 异步汇款
     */
    @Async
    public void moneyTransfer () {
        // TODO 异步汇款
        logger.info("异步汇款");
    }
}
