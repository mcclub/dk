package com.dk.rest.plat.inter;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String key ;/**上游密钥**/
    private String merchantNo;/**上游商户号**/
    private String channelId;

    private Long userId;//用户id
    private Long subId;//小类id
    private String rate;//费率
    private String fee;//单笔
    private String receCard;//交易卡
    private String tabNo;//通道标识
    private String selttbank;//储蓄卡联行号
    private String merId ;//用户商户编号
    private String subUserId ;//通道用户id

    /**
     * 支付流程
     * @param jsonObject
     */
    public RestResult QuickProcess(JSONObject jsonObject) throws Exception{
        RestResult restResult = new RestResult();
        logger.info("支付流程接收到jsonObject的参数为:{}",jsonObject);
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
        /**
         * 查询储蓄卡
         */
        Map<String,Object> mapcard = new HashMap<>();
        mapcard.put("userId",userId);
        mapcard.put("type","01");
        CardInfo cardInfo = userServiceImpl.queryCard(mapcard);
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
        SubUser subUser = subchannelService.querySubuser(map);

        JSONObject quicJson = new JSONObject();
        if(subUser == null){//如果查询结果为空，则说明为注册
            subUserId = "DH_"+cardInfo.getIdentity();

            if("tftkj".equals(tabNo)){//腾付通快捷
                quicJson.put("channel_id",channelId);//通道编号
                quicJson.put("name",jsonObject.getString("name"));//真实姓名
                quicJson.put("id_card",jsonObject.getString("id_card"));//身份证号
                quicJson.put("card_no",jsonObject.getString("card_no"));//结算银行卡号
                quicJson.put("mobile",jsonObject.getString("mobile"));//银行预留手机号
                quicJson.put("user_ip",jsonObject.getString("user_ip"));//用户ip
                JSONObject jsonregis = tenfuTongReplaceApi.TFTregister(quicJson);
            }else if("beiduo".equals(tabNo)){//快捷K3
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
        }else{//若存在则说明已注册，进行修改费率
            if("tftkj".equals(tabNo)){//腾付通快捷

            }else if("beiduo".equals(tabNo)) {//快捷K3
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
        }
        /**
         * 查询交易卡信息
         */
        Map<String,Object> mapcardPay = new HashMap<>();
        mapcardPay.put("userId",userId);
        mapcardPay.put("cardCode",receCard);
        CardInfo info = userServiceImpl.queryCard(mapcardPay);

        /**
         * 查询交易卡联行号
         */
        Map<String,Object> bankMap1 = new HashMap<>();
        bankMap1.put("bankCode",info.getBankCode());//银行简称编码
        List<Bank> bank1 = bankService.query(bankMap1);
        String receBank = "";
        if(bank1 != null){
            receBank = bank1.get(0).getBankNumber();
        }

        Long states = 4L;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)
        String msg = "";//描述

        if("tftkj".equals(tabNo)){//腾付通快捷

        }else if("beiduo".equals(tabNo)) {//快捷K3
            jsonObject.put("merchantCode",merId);//商户编号
            jsonObject.put("bankCode",info.getBankCode());//银行简称
            jsonObject.put("unionBankCode",receBank);//交易卡联行号
            jsonObject.put("cardCode",receCard);//交易卡号
            jsonObject.put("cardPhone",info.getPhone());//交易卡手机号
            jsonObject.put("validDate",info.getValid());//日期
            jsonObject.put("cvv2",info.getCvv());//安全码
            jsonObject.put("realName",info.getRealName());//真实姓名
            jsonObject.put("idCard",info.getIdentity());//身份证号
            jsonObject.put("subuserId",subUserId);//通道用户id
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
                    String orderCode = data.getString("orderCode");//订单号
                    resjson.put("payUrl",payUrl);
                    resjson.put("orderNo",orderCode);
                    resjson.put("resultType",2);//返回类型（1短信验证，2返回url地址,）
                }
                restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"订单提交成功",resjson);
            }else{
                states = 2L;
                restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,msg,new JSONObject());
            }
        }
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
}
