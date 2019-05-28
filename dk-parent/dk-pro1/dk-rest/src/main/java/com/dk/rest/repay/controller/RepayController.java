package com.dk.rest.repay.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Pageable;
import com.common.controller.BaseController;
import com.common.utils.StringUtil;
import com.dk.provider.repay.service.ReceiveRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/repay")
public class RepayController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(RepayController.class);

    @Resource
    private ReceiveRecordService receiveRecordService;


    /**
     * 代还签约公共接口
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/signing"},method = RequestMethod.POST)
    public RestResult signing(JSONObject jsonObject){
        String method = "signing";
        logger.info("进入RepayController的"+method+"方法,参数为:{}",jsonObject);
        try {
            /**
             * 大类通道id
             * 根据卡号和用户id查询是否在该大类通道签约
             */

            return getRestResult("","",new JSONObject());
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 根据信用卡号查询代还金额统计(最近一笔记录即可)
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/statis"},method = RequestMethod.POST)
    public RestResult statis(JSONObject jsonObject){
        String method = "statis";
        logger.info("进入RepayController的"+method+"方法,参数为:{}",jsonObject);
        try {
            //
            return getRestResult("","",new JSONObject());
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }


    @ResponseBody
    @RequestMapping(value = {"/pageHistory"},method = RequestMethod.POST)
    public RestResult pageHistory(@RequestBody Map map){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"pageHistory"+"方法,参数为:{}",map);
        try {
            if (StringUtil.isNotEmpty(map.get("userId"))) {
                Pageable pageable = new Pageable();
                if (StringUtil.isNotEmpty(map.get("pageNumber"))) {
                    pageable.setPageNumber((int)map.get("pageNumber"));
                }
                if (StringUtil.isNotEmpty(map.get("pageSize"))) {
                    pageable.setPageSize((int)map.get("pageSize"));
                }
                return receiveRecordService.pageHistory(map,pageable);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
        }catch (Exception e){
            logger.error("pageHistory"+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }
}
