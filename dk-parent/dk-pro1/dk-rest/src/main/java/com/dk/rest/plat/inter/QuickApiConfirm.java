package com.dk.rest.plat.inter;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.plat.entity.SubUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.provider.user.entity.Bank;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.BankService;
import com.dk.provider.user.service.IUserService;
import com.dk.rest.api.inter.quick.BeiduoQuickPayApi;
import com.dk.rest.api.inter.quick.TenfuTongReplaceApi;
import com.dk.rest.api.inter.quick.XSquickPayApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ROUND_UNNECESSARY;


/**
 * 快捷订单确认公共类
 */
@Service
public class QuickApiConfirm {
    private Logger logger = LoggerFactory.getLogger(QuickApiConfirm.class);

    @Resource
    private RedisCacheService redisCacheService;

    @Resource
    private ReceiveRecordService receiveRecordService;

    @Resource
    private SubchannelService subchannelService;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private BankService bankService;

    @Resource
    private XSquickPayApi xSquickPayApi;

    @Resource
    private BeiduoQuickPayApi beiduoQuickPayApi;

    @Resource
    private TenfuTongReplaceApi tenfuTongReplaceApi;
    private String key ;/**上游密钥**/
    private String merchantNo;/**上游商户号**/
    private String channelId;

    private String tabNo;//通道标记
    private String orderNo;//订单号
    private Long states;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)
    private String msg;//描述
    private Long userId;//用户id
    private String receCard;//交易卡
    private CardInfo info;//交易卡信息
    private String receBank;//交易卡联行号
    private SubUser subUser;//小类通道用户信息
    private String subId;//小类通道id
    private CardInfo cardInfo;//储蓄卡信息
    private String subUserId ;//通道用户id
    private String smsCode;//验证码
    private List<ReceiveRecord> receiveRecordList;//订单信息


    /**
     * 订单确认流程
     * @param jsonObject
     */
    public RestResult QuickConfirm(JSONObject jsonObject) throws Exception{
        logger.info("进入QuickApiConfirm订单确认流程的参数jsonObject为:{}",jsonObject);
        RestResult restResult = new RestResult();
        orderNo = jsonObject.getString("orderNo");
        smsCode = jsonObject.getString("smsCode");

        /**
         * 查询订单信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        receiveRecordList = receiveRecordService.query(map);
        if(receiveRecordList ==null){
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到订单号",new JSONObject());
        }
        receCard = receiveRecordList.get(0).getReceCard();
        userId = Long.valueOf(receiveRecordList.get(0).getUserId());
        subId = receiveRecordList.get(0).getSubId();
        /**
         * 查询小类通道信息
         */
        Map<String,Object> map1=new HashMap<>();
        map1.put("subId",receiveRecordList.get(0).getSubId());
        List<Subchannel> subchannelList = subchannelService.query(map1);
        if(subchannelList == null){
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到路由通道",new JSONObject());
        }
        tabNo = subchannelList.get(0).getTabNo();
        channelId = subchannelList.get(0).getExpand();
        key = subchannelList.get(0).getMerKey();
        merchantNo = subchannelList.get(0).getMerNo();

        if("beiduo".equals(tabNo) || "tftkj".equals(tabNo)){//快捷K3、腾付通快捷
            /**
             * 查询储蓄卡
             */
            Map<String,Object> mapcard = new HashMap<>();
            mapcard.put("userId",userId);
            mapcard.put("type","01");
            cardInfo = userServiceImpl.queryCard(mapcard);
            if(cardInfo == null){
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"未绑定储蓄卡",new JSONObject());
            }

            /**
             * 查询用户是否在该通道注册
             */
            Map<String,Object> mapsubUser = new HashMap<>();
            mapsubUser.put("userId",userId);
            mapsubUser.put("cardNo",cardInfo.getCardCode());//到账储蓄卡
            mapsubUser.put("subId",subId);
            subUser = subchannelService.querySubuser(mapsubUser);
            subUserId = subUser.getSubuserId();
            /**
             * 查询交易卡信息
             */
            Map<String,Object> mapcardPay = new HashMap<>();
            mapcardPay.put("userId",userId);
            mapcardPay.put("cardCode",receCard);
            info = userServiceImpl.queryCard(mapcardPay);
            if(info == null){
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到交易卡",new JSONObject());
            }

            /**
             * 查询交易卡联行号
             */
            Map<String,Object> bankMap1 = new HashMap<>();
            bankMap1.put("bankCode",info.getBankCode());//银行简称编码
            List<Bank> bank1 = bankService.query(bankMap1);
            if(bank1 != null){
                receBank = bank1.get(0).getBankNumber();
            }
        }

        jsonObject.put("channelId",channelId);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        if("xskjk1".equals(tabNo)) {//新生快捷K1
            restResult = xsquickConfim(jsonObject);
        }else if("beiduo".equals(tabNo)){//快捷K3
            restResult = beiduoQuiConfim(jsonObject);
        }else if("tftkj".equals(tabNo)){//腾付通快捷
            restResult = tengfutongQuiConfim(jsonObject);
        }

        /**
         * 修改订单状态和描述
         */
        ReceiveRecord updaRece = new ReceiveRecord();
        updaRece.setOrderNo(orderNo);
        updaRece.setStates(states);
        updaRece.setOrderDesc(msg);
        receiveRecordService.updateStates(updaRece);

        return restResult;
    }

    /**
     * 新生快捷K1订单确认
     * @param jsonObject
     * @return
     */
    public RestResult xsquickConfim(JSONObject jsonObject) throws Exception{
        RestResult restResult = new RestResult();
        JSONObject resjson = new JSONObject();

        jsonObject.put("receCard",receCard);
        JSONObject payJson = xSquickPayApi.XsQuickconfirm(jsonObject);
        msg = payJson.getString("msg");
        states = 2L;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)
        if(payJson.get("code").equals("000")) {//成功
            if(payJson.get("data") !=null){
                JSONObject dataJson = payJson.getJSONObject("data");

                Long status = payJson.getLong("status");//返回的订单状态(1：成功 其他失败)
                if(status ==1){
                    if("0000".equals(dataJson.getString("orderStatus"))){//data子字段，订单状态	4444 失败
                        states = 1L;
                    }else{
                        states = 2L;//0处理中 需要异步回调通知
                    }
                }else{
                    states = 2L;
                }
                resjson.put("orderNo",orderNo);
                resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）
            }
            restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单确认成功,具体结果以银行扣款为准",resjson);
        }else{
            states = 2L;
            return restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"订单确认失败",new JSONObject());
        }
        return restResult;
    }

    /**
     * beiduo快捷订单确认
     * @param jsonObject
     * @return
     */
    public RestResult beiduoQuiConfim(JSONObject jsonObject)throws Exception{
        RestResult restResult = new RestResult();
        String redisSms = redisCacheService.get(info.getPhone());
        if(redisSms == null){
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码已失效",new JSONObject());
        }
        if(!redisSms.equals(smsCode)){//判断验证码是否正确
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码错误",new JSONObject());
        }


        jsonObject.put("amount",receiveRecordList.get(0).getAmount());//金额
        jsonObject.put("merchantCode",subUser.getMerId());//商户编号
        jsonObject.put("bankCode",info.getCardCode());//银行简称
        jsonObject.put("unionBankCode",receBank);//交易卡联行号
        jsonObject.put("cardCode",receCard);//交易卡号
        jsonObject.put("cardPhone",info.getPhone());//交易卡手机号
        jsonObject.put("validDate",info.getValid());//日期
        jsonObject.put("cvv2",info.getCvv());//安全码
        jsonObject.put("realName",info.getRealName());//真实姓名
        jsonObject.put("idCard",info.getIdentity());//身份证号
        jsonObject.put("subuserId",subUser.getSubuserId());//通道用户id
        jsonObject.put("province",receiveRecordList.get(0).getProvince());//省
        jsonObject.put("city",receiveRecordList.get(0).getCity());//市
        JSONObject payJson = beiduoQuickPayApi.BDquickPay(jsonObject);
        msg = payJson.getString("msg");

        JSONObject resjson = new JSONObject();
        if(payJson.get("code").equals("000")) {//成功
            if(payJson.get("data") !=null){
                JSONObject data = payJson.getJSONObject("data");

                Long status = data.getLong("status");//返回的订单状态(1：成功 其他失败)
                if(status ==1){
                    states = 1L;
                }else{
                    states = 2L;
                }
                String payUrl = data.getString("payUrl");//支付地址
                resjson.put("payUrl",payUrl);
                resjson.put("orderNo",orderNo);
                resjson.put("resultType",2);//返回类型（1短信验证，2返回url地址,）
            }
            restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单确认成功,具体结果以银行扣款为准",resjson);
        }else{
            states = 2L;
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"订单确认失败",new JSONObject());
        }
        return restResult;
    }

    /**
     * 腾付通快捷订单确认
     * @param jsonObject
     * @return
     */
    public RestResult tengfutongQuiConfim(JSONObject jsonObject)throws Exception {
        RestResult restResult = new RestResult();
        JSONObject resjson = new JSONObject();
        resjson.put("orderNo",orderNo);
        resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）

        if("1".equals(redisCacheService.get(orderNo+"tftsmsno"))){//获取是否是平台发送验证码标识
            if(!redisCacheService.get(info.getPhone()).equals(smsCode)){//判断验证码是否正确
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"验证码错误或失效",new JSONObject());
            }
        }else{//则验证通道的验证码
            jsonObject.put("channelId",channelId);//通道编号
            jsonObject.put("user_id",subUserId);
            jsonObject.put("card_no",info.getCardCode());
            jsonObject.put("mobile",info.getPhone());
            jsonObject.put("expire_date",info.getValid());
            jsonObject.put("safe_code",info.getCvv());
            jsonObject.put("sms_code",smsCode);
            jsonObject.put("sms_id",redisCacheService.get("sms_id"+orderNo));
            tenfuTongReplaceApi.TFTbind(jsonObject);
        }
        //进行消费操作
        BigDecimal rate =
                new BigDecimal(receiveRecordList.get(0).getRate());//费率
        BigDecimal amt =
                new BigDecimal(receiveRecordList.get(0).getAmount());//交易金额
        amt = amt.multiply(BigDecimal.valueOf(100));
        BigDecimal rateamt = amt.multiply(rate).setScale(2,ROUND_HALF_UP);//费率手续费0.006

        jsonObject.put("channel_id",channelId);//通道编号
        jsonObject.put("user_id",subUserId);//平台账号ID
        jsonObject.put("order_id",orderNo);//订单号,不可重复
        jsonObject.put("card_no",receCard);//银行卡号
        jsonObject.put("amount",amt);//消费金额,单位分
        jsonObject.put("fee",rateamt);//扣除手续费,单位分
        JSONObject jsonpay = tenfuTongReplaceApi.TFTpay(jsonObject);
        if(jsonpay != null){
            if(!jsonpay.containsKey("result")) {
                msg = jsonpay.getString("error_msg");
                String status = jsonpay.getString("status");
                if (status.equals("3") || status.equals("4") || status.equals("5")) {//3成功
                    states = 0L;//成功
                } else if (status.equals("1") || status.equals("2")) {
                    states = 0L;//处理中
                } else {
                    states = 2L;//失败
                }
            }else{
                msg = jsonpay.getString("result");
                states = 2L;//失败
            }
        }

        //进行代付操作
        BigDecimal factamt = amt.subtract(rateamt);//交易金额-手续费 = 代付金额

        BigDecimal fee =
                new BigDecimal(receiveRecordList.get(0).getFee());//单笔手续费
        fee = fee.multiply(BigDecimal.valueOf(100));

        jsonObject.put("channel_id",channelId);//通道编号
        jsonObject.put("user_id",subUserId);//平台账号ID
        jsonObject.put("order_id","DF_"+orderNo);//订单号,不可重复
        jsonObject.put("card_no",cardInfo.getCardCode());//银行卡号(储蓄卡)
        jsonObject.put("amount",factamt);//代付金额,单位分
        jsonObject.put("fee",fee);//扣除手续费,单位分
        JSONObject resJson = tenfuTongReplaceApi.TFTproxypay(jsonObject);
        if(resJson != null) {
            if (!resJson.containsKey("result")) {
                msg = jsonpay.getString("error_msg");
                String status = resJson.getString("status");
                if (status.equals("3") || status.equals("4") || status.equals("5")) {//3成功
                    states = 0L;//成功

                } else if (status.equals("1") || status.equals("2")) {
                    states = 0L;//处理中
                } else {
                    msg = jsonpay.getString("result");
                    states = 2L;//失败
                    return  restResult.setCodeAndMsg(ResultEnume.FAIL,"订单确认失败",new JSONObject());
                }
                restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单确认成功,具体结果以银行扣款为准",resjson);
            } else {
                msg = jsonpay.getString("result");
                states = 2L;//失败
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"订单确认失败",new JSONObject());
            }
        }
        return restResult;
    }

    public static void main(String[] args) {
        BigDecimal rate =
                new BigDecimal(0.0053);//费率
        System.out.println("rate:"+rate);

        BigDecimal amt =
                new BigDecimal(2432.57);//金额
        System.out.println("amt:"+amt);
        BigDecimal fee =
                new BigDecimal(0.53322);//金额
        BigDecimal rateamt = amt.multiply(rate).setScale(2,ROUND_HALF_UP);
        System.out.println("rateamt:"+rateamt);
        rateamt = rateamt.multiply(BigDecimal.valueOf(100));
        System.out.println(("rateamt111:"+rateamt));

        BigDecimal feetotal = rateamt.add(fee).setScale(2,ROUND_HALF_UP);//计算总手续费
        System.out.println("feetotal:"+feetotal);

        String orderNo = "df_1241241284738";
        orderNo = orderNo.substring(3,orderNo.length());
        System.out.println(orderNo);
    }
}
