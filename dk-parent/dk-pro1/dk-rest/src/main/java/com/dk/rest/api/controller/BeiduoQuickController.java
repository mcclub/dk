package com.dk.rest.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.controller.BaseController;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/bdquick")
public class BeiduoQuickController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(BeiduoQuickController.class);

    @Resource
    private ReceiveRecordService receiveRecordService;

    /**
     * 异步通知
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/notify"},method = RequestMethod.POST)
    public String bdnotify(HttpServletRequest request) throws Exception {
        String method = "bdnotify";

        Map<String ,String[]> maps = request.getParameterMap();
        Set<String> keys = maps.keySet();
        Map<String ,String> map1 = new HashMap<>();
        for(String key : keys){
            String[] value = maps.get(key);
            for(String v : value){
                map1.put(key,v);
            }
        }

        if(!map1.isEmpty()){
            logger.info("进入BeiduoQuickController的"+method+"方法,参数map1为:{}",map1);

            //判断订单状态是否处理
            String orderNo = map1.get("orderCode").toString();//订单号
            String merchantCode = map1.get("merchantCode").toString();//商户编号
            String status = map1.get("status").toString();//订单状态 1：成功 其他失败
            String respMsg = map1.get("msg").toString();//消息

            String states = "2";
            if(status.equals("1")){//交易成功
                states = "1";//成功
            }else{
                states = "2";//失败
            }

            Map<String,Object> map = new HashMap<>();
            map.put("orderNo",orderNo);
            List<ReceiveRecord> receiveRecordList = receiveRecordService.query(map);
            if(receiveRecordList.size() >0){
                if(!"1".equals(receiveRecordList.get(0).getStates())){
                    /**
                     * 修改订单状态和描述
                     */
                    ReceiveRecord updaRece = new ReceiveRecord();
                    updaRece.setOrderNo(orderNo);
                    updaRece.setStates(Long.valueOf(states));
                    updaRece.setOrderDesc(respMsg);
                    int updat = receiveRecordService.updateStates(updaRece);

                    /**
                     * 订单成功 计算返佣 并新增 返佣记录
                     */


                    if(updat >0){
                        return "success";
                    }else{
                        return "fail";
                    }
                }
            }
        }
        return "";
    }
}
