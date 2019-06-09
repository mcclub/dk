package com.dk.rest.plat.inter;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.plat.entity.SubUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.SmsRecordService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快捷订单提交公共类
 */
@Service
public class QuickApiProcess {
    private Logger logger = LoggerFactory.getLogger(QuickApiProcess.class);
    @Resource
    private SubchannelService subchannelService;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private BankService bankService;

    @Resource
    private ReceiveRecordService receiveRecordService;

    @Resource
    private BeiduoQuickPayApi beiduoQuickPayApi;

    @Resource
    private TenfuTongReplaceApi tenfuTongReplaceApi;

    @Resource
    private XSquickPayApi xSquickPayApi;

    @Resource
    private SmsRecordService smsRecordService;

    @Resource
    private RedisCacheService redisCacheService;

    private String key ;/**上游密钥**/
    private String merchantNo;/**上游商户号**/
    private String channelId;

    private String orderNo;//订单号
    private Long userId;//用户id
    private Long subId;//小类id
    private String rate;//费率
    private String fee;//单笔
    private String receCard;//交易卡
    private String tabNo;//通道标识
    private String selttbank;//储蓄卡联行号
    private String merId ;//用户商户编号
    private String subUserId ;//通道用户id
    private CardInfo cardInfo;//查询储蓄卡信息
    private SubUser subUser;//用户通道信息
    private CardInfo info;//交易卡信息
    private String receBank;//交易卡联行号
    private String msg ;//描述
    private Long states;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)

    /**
     * 订单提交流程
     * @param jsonObject
     */
    public RestResult QuickProcess(JSONObject jsonObject) throws Exception{
        RestResult restResult = new RestResult();
        logger.info("支付流程接收到jsonObject的参数为:{}",jsonObject);
        orderNo = jsonObject.getString("orderNo");//订单号
        userId = jsonObject.getLong("userId");//用户id
        subId = jsonObject.getLong("subId");//小类id
        rate = jsonObject.getString("rate");//费率
        fee = jsonObject.getString("fee");//单笔
        receCard = jsonObject.getString("receCard");//交易卡
        tabNo = jsonObject.getString("tabNo");//通道标识

        /**
         * 查询小类通道密钥信息
         */
        Map<String,Object> map1=new HashMap<>();
        map1.put("subId",subId);
        List<Subchannel> subchannelList = subchannelService.query(map1);
        if(subchannelList != null){
            merchantNo = subchannelList.get(0).getMerNo();
            key = subchannelList.get(0).getMerKey();
            channelId = subchannelList.get(0).getExpand();
        }else{
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到路由通道",new JSONObject());
        }
        jsonObject.put("merchantNo",merchantNo);//上游商户号
        jsonObject.put("key",key);//上游密钥
        jsonObject.put("channelId",channelId);
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
         * 查询储蓄卡联行号
         */
        Map<String,Object> bankMap = new HashMap<>();
        bankMap.put("bankCode",cardInfo.getBankCode());
        List<Bank> bank = bankService.query(bankMap);
        if(bank != null){
            selttbank = bank.get(0).getBankNumber();
        }

        /**
         * 查询用户是否在该通道注册
         */
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("cardNo",cardInfo.getCardCode());//到账储蓄卡
        map.put("subId",jsonObject.get("subId"));
        subUser = subchannelService.querySubuser(map);
        viewRegister(jsonObject);


        /**
         * 查询交易卡信息
         */
        Map<String,Object> mapcardPay = new HashMap<>();
        mapcardPay.put("userId",userId);
        mapcardPay.put("cardCode",receCard);
        info = userServiceImpl.queryCard(mapcardPay);
        if(info == null){
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到绑定信用卡",new JSONObject());
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

        restResult = viewquickPay(jsonObject);

        /**
         * 修改订单状态和描述
         */
        ReceiveRecord updaRece = new ReceiveRecord();
        updaRece.setOrderNo(jsonObject.getString("orderNo"));
        updaRece.setStates(states);
        updaRece.setOrderDesc(msg);
        receiveRecordService.updateStates(updaRece);
        return restResult;
    }

    /**
     * 通道是否注册
     * @throws Exception
     */
    public void viewRegister(JSONObject jsonObject) throws Exception{
        if(subUser == null){//如果查询结果为空，则说明为注册
            subUserId = "DH_"+cardInfo.getIdentity();
            if("tftkj".equals(tabNo)){//腾付通快捷
                tengfutongReg(jsonObject);
            }else if("beiduo".equals(tabNo)){//快捷K3
                beiduoReg(jsonObject);
            }
        }else{//若存在则说明已注册，进行修改费率
            if("tftkj".equals(tabNo)){//腾付通快捷
                subUserId = subUser.getSubuserId();
            }else if("beiduo".equals(tabNo)) {//快捷K3
                beiduoModify(jsonObject);
            }
        }
    }

    /**
     * 快捷订单提交
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public RestResult viewquickPay(JSONObject jsonObject) throws Exception{
        RestResult restResult = new RestResult();
        jsonObject.put("merchantNo",merchantNo);//上游商户号
        jsonObject.put("key",key);//上游密钥
        jsonObject.put("channelId",channelId);
        states = 4L;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)

        if("tftkj".equals(tabNo)){//腾付通快捷
            restResult =  tengfutongQuiPay(jsonObject);
        }else if("beiduo".equals(tabNo)) {//快捷K3
            restResult = beiduoQuiPay(jsonObject);
        }else if("xskjk1".equals(tabNo)){//新生快捷K1
            restResult = xinshengQuiPay(jsonObject);
        }
        return restResult;
    }

    /**
     * 腾付通注册
     * @param quicJson
     * @throws Exception
     */
    public void tengfutongReg(JSONObject quicJson) throws Exception{
        quicJson.put("channel_id",channelId);//通道编号
        quicJson.put("name",cardInfo.getRealName());//真实姓名
        quicJson.put("id_card",cardInfo.getIdentity());//身份证号
        quicJson.put("card_no",cardInfo.getCardCode());//结算银行卡号
        quicJson.put("mobile",cardInfo.getPhone());//银行预留手机号
        JSONObject jsonregis = tenfuTongReplaceApi.TFTregister(quicJson);
        if(jsonregis != null && jsonregis.containsKey("user_id")) {//成功
            //注册成功保存商户信息
            SubUser subUser1 = new SubUser();
            subUser1.setUserId(userId.toString());//用户id
            subUser1.setName(cardInfo.getRealName());//姓名
            subUser1.setCardNo(cardInfo.getCardCode());//卡号
            subUser1.setBankName(cardInfo.getBankName());//银行名称
            subUser1.setPhone(cardInfo.getPhone());//手机号
            subUser1.setIdentity(cardInfo.getIdentity());//身份证
            subUser1.setSubId(subId);//小类通道id
            subUser1.setSubuserId(jsonregis.getString("user_id"));//通道用户id
            subchannelService.insertSubuser(subUser1);
            subUserId = jsonregis.getString("user_id");
        }
    }

    /**
     * 腾付通快捷 查看是否绑卡,已绑卡则发送平台验证码，
     * @param jsonObject
     * @return
     */
    public RestResult tengfutongQuiPay(JSONObject jsonObject)throws Exception{
        RestResult restResult = new RestResult();
        JSONObject resjson = new JSONObject();
        resjson.put("orderNo",orderNo);
        resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）

        jsonObject.put("channelId",channelId);//通道编号
        jsonObject.put("user_id",subUserId);
        jsonObject.put("card_no",info.getCardCode());
        jsonObject.put("mobile",info.getPhone());
        jsonObject.put("expire_date",info.getValid());
        jsonObject.put("safe_code",info.getCvv());
        JSONObject jsonregis = tenfuTongReplaceApi.TFTbind(jsonObject);
        if(jsonregis != null && jsonregis.containsKey("card_id")){//
            redisCacheService.set(orderNo+"tftsmsno","1",5L);//缓存是否是平台发送验证码标识(标识1)
            //发送平台验证码
            JSONObject jsonSms = new JSONObject();
            jsonSms.put("phone",info.getPhone());
            if(smsRecordService.insertOne(jsonSms) > 0){
                msg = "发送验证码成功";
                restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单提交成功",resjson);
            }else{
                msg = "发送验证码失败";
                restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"订单提交失败",new JSONObject());
            }
        }else if(jsonregis != null && jsonregis.containsKey("sms_id")){
            msg = "发送验证码成功";
            redisCacheService.set("sms_id"+orderNo,jsonregis.getString("sms_id"));//缓存验证码序号
            restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单提交成功",resjson);
        }else{
            msg = "发送验证码失败";
            restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"订单提交失败",new JSONObject());
        }
        return restResult;
    }

    /**
     * beiduo快捷注册
     * @param quicJson
     * @throws Exception
     */
    public void beiduoReg(JSONObject quicJson) throws Exception{
        quicJson.put("rate",rate);//费率
        quicJson.put("extraFee",fee);//单笔手续费
        quicJson.put("phone",cardInfo.getPhone());//结算卡预留手机号
        quicJson.put("settleCard",cardInfo.getCardCode());//结算卡
        quicJson.put("realName",cardInfo.getRealName());//姓名
        quicJson.put("idCard",cardInfo.getIdentity());//身份证号
        quicJson.put("bankCode",selttbank);//联行号
        quicJson.put("bankName",cardInfo.getBankName());//银行名称
        quicJson.put("userId",subUserId);//用户ID
        JSONObject jsonregis = beiduoQuickPayApi.BDquickRegist(quicJson);
        if(jsonregis.get("code").equals("000")) {//成功
            JSONObject data = jsonregis.getJSONObject("data");
            //注册成功保存商户信息
            SubUser subUser1 = new SubUser();
            subUser1.setUserId(userId.toString());//用户id
            subUser1.setName(cardInfo.getRealName());//姓名
            subUser1.setCardNo(cardInfo.getCardCode());//卡号
            subUser1.setBankName(cardInfo.getBankName());//银行名称
            subUser1.setPhone(cardInfo.getPhone());//手机号
            subUser1.setIdentity(cardInfo.getIdentity());//身份证
            subUser1.setSubuserId(subUserId);//小类通道用户id
            subUser1.setSubId(subId);//小类通道id
            subUser1.setRate(rate);//用户费率
            subUser1.setFee(fee);//用户单笔手续费(元)
            subUser1.setMerId(data.getString("merchantCode"));//用户商户编号
            subUser1.setMerKey(data.getString("merchantKey"));//用户商户密钥
            subchannelService.insertSubuser(subUser1);

            merId = subUser1.getMerId();//用户商户编号
        }
    }

    /**
     * beiduo快捷修改
     * @param quicJson
     * @throws Exception
     */
    public void beiduoModify(JSONObject quicJson) throws Exception{
        merId = subUser.getMerId();//用户商户编号
        subUserId = subUser.getSubuserId();//通道用户id
        quicJson.put("rate", rate);//费率
        quicJson.put("extraFee", fee);//单笔手续费
        quicJson.put("merchantCode", merId);//商户编号
        quicJson.put("cardPhone", cardInfo.getPhone());//结算卡预留手机号
        quicJson.put("cardCode", cardInfo.getCardCode());//结算卡
        quicJson.put("bankCode", selttbank);//联行号
        quicJson.put("bankName", cardInfo.getBankName());//银行名称
        JSONObject jsonmodify = beiduoQuickPayApi.BDquickModify(quicJson);
        if (jsonmodify.get("code").equals("000")) {//成功
            //修改结算信息 费率等
            SubUser subUser2 = new SubUser();
            subUser2.setId(subUser.getId());//主键id
            subUser2.setRate(rate);
            subUser2.setFee(fee);
            subUser2.setPhone(cardInfo.getPhone());
            subUser2.setCardNo(cardInfo.getCardCode());
            subUser2.setBankName(cardInfo.getBankName());
            subchannelService.updateSubuser(subUser2);
        }
    }

    /**
     * beiduo快捷支付订单提交
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public RestResult beiduoQuiPay(JSONObject jsonObject) throws Exception{
        RestResult restResult = new RestResult();
        JSONObject resjson = new JSONObject();
        resjson.put("orderNo",orderNo);
        resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）

        //发送平台验证码
        JSONObject jsonSms = new JSONObject();
        jsonSms.put("phone",info.getPhone());
        if(smsRecordService.insertOne(jsonSms) > 0){
            msg = "发送验证码成功";
            restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单提交成功",resjson);
        }else{
            restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"订单提交失败",new JSONObject());
            msg = "发送验证码失败";
        }
        return restResult;
    }

    /**
     * 新生快捷K1订单提交
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public RestResult xinshengQuiPay(JSONObject jsonObject) throws Exception {
        RestResult restResult = new RestResult();
        JSONObject resjson = new JSONObject();
        resjson.put("orderNo",orderNo);
        resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）
        //需要的参数
        jsonObject.put("orderCode",orderNo);//唯一订单号
        jsonObject.put("creditCard",info.getCardCode());//信用卡卡号
        jsonObject.put("amount",jsonObject.getString("amount"));//订单金额
        jsonObject.put("realName",info.getRealName());//用户真实姓名
        jsonObject.put("creditValidDate",info.getValid());//信用卡有效期
        jsonObject.put("cvv2",info.getCvv());//安全码
        jsonObject.put("creditPhone",info.getPhone());//信用卡银行预留电话
        jsonObject.put("creditBankName",info.getBankName());//信用卡所在银行名称
        jsonObject.put("idCard",info.getIdentity());//身份证号
        jsonObject.put("settleCard",cardInfo.getCardCode());//借记卡卡号
        jsonObject.put("settlePhone",cardInfo.getPhone());//借记卡银行预留电话
        jsonObject.put("userId",subUserId);//用户ID
        jsonObject.put("province",jsonObject.getString("province"));//省
        jsonObject.put("city",jsonObject.getString("city"));//市
        jsonObject.put("rate",rate);//费率
        jsonObject.put("extraFee",fee);//额外手续费
        JSONObject payJson = xSquickPayApi.XsQuickapply(jsonObject);
        msg = payJson.getString("msg");
        if(payJson.get("code").equals("000")) {//成功
            if(payJson.get("data") !=null){

                JSONObject dataJson = payJson.getJSONObject("data");
                Long status = payJson.getLong("status");//返回的订单状态(1：成功 其他失败)
                if(status ==1){
                    states = 0L;//0处理中 需要异步回调通知
                }else{
                    states = 2L;
                }
                String upOrderCode = dataJson.getString("upOrderCode");//申请返回的平台订单号
                String orderCode = dataJson.getString("orderCode");//订单号
                redisCacheService.set("XsK1up"+receCard,upOrderCode,2*60L);
                redisCacheService.set("XsK1od"+receCard,orderCode,2*60L);
            }
            restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单提交成功",resjson);
        }else{
            states = 2L;
            restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"订单提交失败",new JSONObject());
        }
        return restResult;
    }



}
