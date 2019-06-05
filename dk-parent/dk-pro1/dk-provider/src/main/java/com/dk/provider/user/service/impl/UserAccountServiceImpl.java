package com.dk.provider.user.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.common.utils.EncryptionUtil;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.UserAccount;
import com.dk.provider.user.entity.Withdraw;
import com.dk.provider.user.entity.WithdrawRecord;
import com.dk.provider.user.mapper.UserAccountMapper;
import com.dk.provider.user.mapper.WithdrawRecordMapper;
import com.dk.provider.user.service.IUserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("userAccountServiceImpl")
public class UserAccountServiceImpl extends BaseServiceImpl<UserAccount> implements IUserAccountService {
    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private WithdrawRecordMapper withdrawRecordMapper;


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
            UserAccount userAccount = (UserAccount) result.getData();
            if (!StringUtil.isEmpty(userAccount.getPassword())) {
                result.setData(true);
                return result;
            } else {
                result.setData(false);
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
        String orderNo = CommonUtils.getOrderNo();
        map.put("userId",withdraw.getUserId());
        //查询用户账户余额
        UserAccount userAccount = userAccountMapper.queryByUserId(map);
        if (StringUtil.isNotEmpty(userAccount)) {
            //如果未设置交易密码则提示设置交易密码
            if (StringUtil.isEmpty(userAccount.getPassword())) {
                return restResult.setCodeAndMsg(ResultEnume.PROCESSFAIL,"请先设置交易密码");
            }
            //如果提现金额大于账户余额
            if (withdraw.getAmount() < Double.parseDouble(userAccount.getBalance())) {
                int num = userAccountMapper.deductingBalance(withdraw);
                if (num > 0) {
                    WithdrawRecord withdrawRecord = new WithdrawRecord();
                    withdrawRecord.setAmount(String.valueOf(withdraw.getAmount()));
                    withdrawRecord.setOrderNo(orderNo);
                    withdrawRecord.setUserId(withdraw.getUserId());
                    withdrawRecord.setRate("0.001");
                    withdrawRecord.setHandlingFee(String.valueOf(0.001 * withdraw.getAmount()));
                    withdrawRecord.setCreateTime(new Date());
                    withdrawRecord.setStatus(2l);
                    int insertWithdrawNum = withdrawRecordMapper.insert(withdrawRecord);
                    if (insertWithdrawNum > 0) {
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
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"提现失败");
        }
    }
}
