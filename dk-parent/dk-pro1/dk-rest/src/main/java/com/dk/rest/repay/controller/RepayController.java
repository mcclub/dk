package com.dk.rest.repay.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repay")
public class RepayController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(RepayController.class);


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
}
