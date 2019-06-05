package com.dk.rest.user.controller;


import com.alibaba.fastjson.JSON;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.common.controller.BaseController;
import com.common.utils.StringUtil;
import com.dk.provider.user.entity.AccountDetail;
import com.dk.provider.user.service.IAccountDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 账户详情
 */
@RestController
@RequestMapping("/accountDetail")
public class AccountDetailController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(AccountDetailController.class);

    @Resource
    private IAccountDetailService accountDetailServiceImpl;
    /**
     * 分页查询账户详情
     * @return
     */
    @RequestMapping("/page")
    public RestResult page (@RequestBody Map map) {
        try{
            logger.info("start begain accountDetail page" );
            RestResult restResult = new RestResult();
            Pageable pageable = new Pageable();
            if (StringUtil.isNotEmpty(map.get("pageSize"))) {
                pageable.setPageSize(Integer.parseInt((String) map.get("pageSize")));
            }
            if (StringUtil.isNotEmpty(map.get("pageNumber"))) {
                pageable.setPageNumber(Integer.parseInt((String)map.get("pageNumber")));
            }
            if (!StringUtil.isNotEmpty(map.get("userId"))) {
                restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
            Page<AccountDetail> page = accountDetailServiceImpl.page(map,pageable);
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",page);
        } catch (Exception e) {
            logger.info("message:"+e.getMessage());
            return getFail();
        }
    }
}
