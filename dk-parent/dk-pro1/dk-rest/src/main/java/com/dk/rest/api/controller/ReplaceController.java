package com.dk.rest.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.controller.BaseController;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.service.IPaymentDetailService;
import com.dk.provider.repay.service.RepayPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/replace")
public class ReplaceController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ReplaceController.class);

    @Resource
    private IPaymentDetailService paymentDetailServiceImpl;

    @Resource
    private RepayPlanService repayPlanService;

    /**
     * 1 新生代还收款请求的异步通知
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/xsnotify"} ,method = RequestMethod.POST)
    public String Xsnotify(@RequestBody JSONObject jsonObject) throws Exception{
        logger.info("进入ReplaceController的Xsnotify方法,参数jsonObject:{}",jsonObject);
        Map<String,Object> map1 = (Map<String,Object>)jsonObject;
        if(!map1.isEmpty()){
            logger.info("进入ReplaceController的Xsnotify方法,参数map1为:{}",map1);

            //判断订单状态是否处理
            String orderCode = map1.get("orderCode").toString();//收款请求订单号
            String resultCode = map1.get("resultCode").toString();//处理结果
            String errorCode = map1.get("errorCode").toString();//错误码
            String errorMsg = map1.get("errorMsg").toString();//错误信息
            String upOrderCode = map1.get("upOrderCode").toString();//平台订单号
            String amount =  map1.get("amount").toString();//金额

            String states = "4";
            if(resultCode.equals("0000")){//0000 成功 4444 失败 9999 处理中
                states = "2";//0000 成功
            }else if(resultCode.equals("4444")){
                states = "4";//4444 失败
            }else if(resultCode.equals("9999")){
                states = "1";//9999 处理中
            }

            JSONObject json = new JSONObject();
            json.put("orderNo",orderCode);
            json.put("respMsg",errorMsg);
            json.put("states",states);
            json.put("orderType",2);//订单类型(1快捷，2代还)
            int updat = paymentDetailServiceImpl.updateDetailState(json);
            if(updat >0){
                return "success";
            }else{
                return "fail";
            }

        }
        return "fail";
    }

    /**
     *2 新生代还付款请求的异步通知
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/xspaynotify"} ,method = RequestMethod.POST)
    public String Xspaynotify(@RequestBody JSONObject jsonObject) throws Exception{
        logger.info("进入ReplaceController的Xspaynotify方法,参数jsonObject:{}",jsonObject);
        Map<String,Object> map1 = (Map<String,Object>)jsonObject;
        if(!map1.isEmpty()){
            logger.info("进入ReplaceController的xspaynotify方法,参数map1为:{}",map1);

            //判断订单状态是否处理
            String orderCode = map1.get("orderCode").toString();//收款请求订单号
            String resultCode = map1.get("resultCode").toString();//处理结果
            String errorCode = map1.get("errorCode").toString();//错误码
            String errorMsg = map1.get("errorMsg").toString();//错误信息
            String upOrderCode = map1.get("upOrderCode").toString();//平台订单号
            String amount =  map1.get("amount").toString();//金额

            String states = "4";
            if(resultCode.equals("0000")){//0000 成功 4444 失败 9999 处理中
                states = "2";//0000 成功
            }else if(resultCode.equals("4444")){
                states = "4";//4444 失败
            }else if(resultCode.equals("9999")){
                states = "1";//9999 处理中
            }

            Map<String ,String> map = new HashMap<>();
            map.put("orderNo",orderCode);
            List<PaymentDetail> paymentDetailList = paymentDetailServiceImpl.query(map);
            if(!paymentDetailList.isEmpty()){
                String pmStates = paymentDetailList.get(0).getStatus().toString();
                logger.info("平台订单状态pmStates:{}",pmStates);
                if(!pmStates.equals("1")) {//平台成功
                    /**
                     * 修改已还金额
                     */
                    Map<String ,Object> maprp = new HashMap<>();
                    maprp.put("id",paymentDetailList.get(0).getPlanId());
                    maprp.put("returnAmt",paymentDetailList.get(0).getAmount());
                    repayPlanService.updreturnAmt(maprp);
                    /**
                     * 根据计划id 修改该笔详情的订单号
                     */
                    PaymentDetail paymentDetail1 = new PaymentDetail();
                    paymentDetail1.setId(paymentDetailList.get(0).getId());
                    paymentDetail1.setRemark(errorMsg);
                    paymentDetail1.setStatus(Long.valueOf(states));
                    int updat = paymentDetailServiceImpl.update(paymentDetail1);
                    if(updat >0){
                        return "success";
                    }else{
                        return "fail";
                    }
                }
            }
        }
        return "fail";
    }

}
