package com.dk.rest.rake.controller;


import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.StringUtil;
import com.dk.provider.rake.service.IRakeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;


/**
 * 返佣查询Controller
 */
@RestController
@RequestMapping("/rebate")
public class RebateController {
    private Logger logger = LoggerFactory.getLogger(RebateController.class);

    @Resource
    private IRakeRecordService rakeRecordServiceImpl;


    @RequestMapping("/searchAll")
    public RestResult searchAll (@RequestBody Map map) {
        logger.info("banding searchAll start 。。。");
        RestResult restResult = new RestResult();
        try {
            if (StringUtil.isNotEmpty(map)) {
                if (StringUtil.isNotEmpty(map.get("userId"))) {
                    return rakeRecordServiceImpl.queryRebate(map);
                } else {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空。。。");
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误!");
            }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"系统繁忙!");
        }
    }


    @RequestMapping("/friendList")
    public RestResult friendList (@RequestBody Map map) {
        logger.info("banding friendList start 。。。");
        RestResult restResult = new RestResult();
        try {
            if (StringUtil.isNotEmpty(map)) {
                if (StringUtil.isNotEmpty(map.get("userId"))) {
                    return rakeRecordServiceImpl.friendList(map);
                } else {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空。。。");
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误!");
            }
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"系统繁忙!");
        }
    }
}
