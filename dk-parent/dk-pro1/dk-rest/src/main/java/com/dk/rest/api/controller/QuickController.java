package com.dk.rest.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.controller.BaseController;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/quick")
public class QuickController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(QuickController.class);

    @Resource
    private ReceiveRecordService receiveRecordService;

    @Resource
    private IUserService userService;

    /**
     * 快捷K3异步通知
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/bdnotify"},method = RequestMethod.POST)
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
            logger.info("进入QuickController的"+method+"方法,参数map1为:{}",map1);

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

            JSONObject json = new JSONObject();
            json.put("orderNo",orderNo);
            json.put("respMsg",respMsg);
            json.put("states",states);
            json.put("orderType",1);//订单类型(1快捷，2代还)
            int updat = receiveRecordService.updateReceiveOrder(json);

            if(updat >0){
                return "success";
            }else{
                return "fail";
            }
            /*Map<String,Object> map = new HashMap<>();
            map.put("orderNo",orderNo);
            List<ReceiveRecord> receiveRecordList = receiveRecordService.query(map);
            if(receiveRecordList.size() >0){
                if(!"1".equals(receiveRecordList.get(0).getStates())){
                    *//**
                     * 修改订单状态和描述
                     *//*
                    ReceiveRecord updaRece = new ReceiveRecord();
                    updaRece.setOrderNo(orderNo);
                    updaRece.setStates(Long.valueOf(states));
                    updaRece.setOrderDesc(respMsg);
                    int updat = receiveRecordService.updateStates(updaRece);

                    *//**
                     * 订单成功 计算返佣 并新增 返佣记录
                     *//*


                    if(updat >0){
                        return "success";
                    }else{
                        return "fail";
                    }
                }
            }*/
        }
        return "fail";
    }

    /**
     * 新生快捷K1消费异步通知
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/xsnotify"} ,method = RequestMethod.POST)
    public String Xsquicknotify(@RequestBody JSONObject jsonObject) throws Exception{
        logger.info("进入QuickController的xsnotify方法,参数jsonObject:{}",jsonObject);
        Map<String,Object> map1 = (Map<String,Object>)jsonObject;
        if(!map1.isEmpty()){
            logger.info("进入QuickController的xsnotify方法,参数map1为:{}",map1);

            //判断订单状态是否处理
            String orderNo = map1.get("orderCode").toString();
            String upOrderCode = map1.get("upOrderCode").toString();//平台订单号
            String resultCode = map1.get("resultCode").toString();//响应码
            String respMsg = map1.get("respMsg").toString();//响应信息
            String settleAmt = map1.get("settleAmt").toString();//实际到账金额

            String states = "2";
            if(resultCode.equals("0000")){//交易成功
                states = "1";//成功
            }else if(resultCode.equals("4444")){//交易失败
                states = "2";//失败
            }else if(resultCode.equals("5555")){//交易失效
                states = "2";//失败
            }else if(resultCode.equals("9999")){//交易进行中
                states = "0";//处理中
            }

            JSONObject json = new JSONObject();
            json.put("orderNo",orderNo);
            json.put("respMsg",respMsg);
            json.put("states",states);
            json.put("orderType",1);//订单类型(1快捷，2代还)
            int updat = receiveRecordService.updateReceiveOrder(json);

            if(updat >0){
                return "success";
            }else{
                return "fail";
            }
        }
        return "fail";
    }

    /**
     * 公共处理订单逻辑
     * @param jsonObject
     * @return
     * @throws Exception
     *//*
    public String getreturnResult(JSONObject jsonObject) throws Exception{
        String orderNo = jsonObject.getString("orderNo");
        String states = jsonObject.getString("states");
        String respMsg = jsonObject.getString("respMsg");

        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        List<ReceiveRecord> receiveRecordList = receiveRecordService.query(map);
        if(receiveRecordList.size() >0){
            if(!"1".equals(receiveRecordList.get(0).getStates())){
                *//**
                 * 修改订单状态和描述
                 *//*
                ReceiveRecord updaRece = new ReceiveRecord();
                updaRece.setOrderNo(orderNo);
                updaRece.setStates(Long.valueOf(states));
                updaRece.setOrderDesc(respMsg);
                int updat = receiveRecordService.updateStates(updaRece);

                *//**
                 * 订单成功 计算返佣 并新增 返佣记录
                 *//*
                if("1".equals(states)){
                    //查询当前人的通道费率和等级
                    String routeId = receiveRecordList.get(0).getRouteId();//大类通道id
                    String userId = receiveRecordList.get(0).getUserId();//用户id
                    String rate = receiveRecordList.get(0).getRate();//刷卡时的费率

                    User user = userService.queryByid(Long.valueOf(userId));
                    if(user != null){
                        Long classId = user.getClassId();//该用户的等级id
                    }
                    *//**
                     * 查询用户等级等级类型(1普通等级,2代理商等级)
                     * 普通等级返给上一级用户
                     * 代理商等级返给上两级用户
                     *//*


                    *//**
                     * 查看用户上级 并查出上级的等级费率
                     *//*
                }


                if(updat >0){
                    return "success";
                }else{
                    return "fail";
                }
            }
        }
        return "fail";
    }*/
}
