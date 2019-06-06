package com.dk.provider.user.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.user.entity.AccountDetail;
import com.dk.provider.user.entity.AccountDetailVO;
import com.dk.provider.user.mapper.AccountDetailMapper;
import com.dk.provider.user.service.IAccountDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 账户详情
 */
@Service
public class AccountDetailServiceImpl extends BaseServiceImpl<AccountDetail> implements IAccountDetailService {
    private Logger logger = LoggerFactory.getLogger(AccountDetailServiceImpl.class);
    @Resource
    private AccountDetailMapper accountDetailMapper;
    @Resource
    public void setSqlMapper (AccountDetailMapper accountDetailMapper)
    {
        super.setBaseMapper (accountDetailMapper);
    }

    @Override
    public RestResult page(Map map, Pageable pageable) {
        RestResult restResult = new RestResult();
        Page<AccountDetail> page = super.findPages(map,pageable);
        List<AccountDetailVO> list = new ArrayList<>();
        if (page.getContent() != null && !page.getContent().isEmpty()) {
            for (AccountDetail bean : page.getContent()) {
                AccountDetailVO accountDetail = new AccountDetailVO();
                //状态
                if (bean.getStatus() == 0) {
                    bean.setStatusStr("失败");
                }else if (bean.getStatus() == 1) {
                    bean.setStatusStr("成功");
                }

                //操作类型
                if (bean.getOperatingType() == 0) {
                    bean.setOperatingTypeStr("快捷");
                }else if (bean.getOperatingType() == 1) {
                    bean.setOperatingTypeStr("代还");
                }

                //交易类型
                if (bean.getStatusTransaction() == 0) {
                    bean.setStatusTransactionStr("入账");
                } else if (bean.getStatusTransaction() == 1) {
                    bean.setStatusTransactionStr("出账");
                }
                BeanUtils.copyProperties(bean,accountDetail);
                list.add(accountDetail);
            }
        }
        Page<AccountDetailVO> result = new Page<>(list,page.getTotal(),page.getPageable());
        return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",result);
    }
}
