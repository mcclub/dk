package com.dk.rest.api.inter;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.dk.provider.plat.entity.RouteUser;
import com.dk.provider.plat.entity.SubUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.provider.user.entity.Bank;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.BankService;
import com.dk.provider.user.service.IUserService;
import com.dk.rest.api.common.CommonApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ascme
 * @ClassName beiduo 快捷api
 * @Date 2019-04-07
 */
@Service
@PropertySource("classpath:application.properties")
public class BeiduoQuickPayApi {
    private Logger logger = LoggerFactory.getLogger(BeiduoQuickPayApi.class);

    @Resource
    private CommonApi commonApi;

    @Resource
    private SubchannelService subchannelService;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private BankService bankService;

    @Resource
    private ReceiveRecordService receiveRecordService;

    /**上游密钥**/
    private String key ;
    /**上游商户号**/
    private String merchantNo;

    /**请求ip**/
    private String reqIp="api.channel.gumids.com";//生产

    @Value("${server.ip}")
    String serverip;

    /**
     * beiduo支付流程
     * @param jsonObject
     */
    public RestResult BDquickProcess(JSONObject jsonObject) throws Exception{
        logger.info("支付流程接收到jsonObject的参数为:{}",jsonObject);
        Long userId = jsonObject.getLong("userId");//用户id
        Long subId = jsonObject.getLong("subId");//小类id
        String rate = jsonObject.getString("rate");//费率
        String fee = jsonObject.getString("fee");//单笔
        String receCard = jsonObject.getString("receCard");//交易卡

        /**
         * 查询小类通道密钥信息
         */
        Map<String,Object> map1=new HashMap<>();
        map1.put("subId",subId);
        List<Subchannel> subchannelList = subchannelService.query(map1);
        if(subchannelList != null){
            merchantNo = subchannelList.get(0).getMerNo();
            key = subchannelList.get(0).getMerKey();
        }
        /**
         * 查询储蓄卡
         */
        Map<String,Object> mapcard = new HashMap<>();
        mapcard.put("userId",userId);
        mapcard.put("type","01");
        CardInfo cardInfo = userServiceImpl.queryCard(mapcard);

        /**
         * 查询储蓄卡联行号
         */
        Map<String,Object> bankMap = new HashMap<>();
        bankMap.put("bankCode",cardInfo.getBankCode());
        List<Bank> bank = bankService.query(bankMap);
        String selttbank = "";
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

        String merId = "";//用户商户编号
        String subUserId = "";//通道用户id

        JSONObject bdquicJson = new JSONObject();
        if(subUser == null){//如果查询结果为空，则说明为注册
            subUserId = "DH_"+cardInfo.getIdentity();
            /**
             * 查询用户绑定储蓄卡信息
             */
            bdquicJson.put("rate",rate);//费率
            bdquicJson.put("extraFee",fee);//单笔手续费
            bdquicJson.put("phone",cardInfo.getPhone());//结算卡预留手机号
            bdquicJson.put("settleCard",cardInfo.getCardCode());//结算卡
            bdquicJson.put("realName",cardInfo.getRealName());//姓名
            bdquicJson.put("idCard",cardInfo.getIdentity());//身份证号
            bdquicJson.put("bankCode",selttbank);//联行号
            bdquicJson.put("bankName",cardInfo.getBankName());//银行名称
            bdquicJson.put("userId",subUserId);//用户ID
            JSONObject jsonregis = BDquickRegist(bdquicJson);
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

        }else{//若存在则说明已注册，进行修改费率
            merId = subUser.getMerId();//用户商户编号
            subUserId = subUser.getSubuserId();//通道用户id

            bdquicJson.put("rate",rate);//费率
            bdquicJson.put("extraFee",fee);//单笔手续费
            bdquicJson.put("merchantCode",merId);//商户编号
            bdquicJson.put("cardPhone",cardInfo.getPhone());//结算卡预留手机号
            bdquicJson.put("cardCode",cardInfo.getCardCode());//结算卡
            bdquicJson.put("bankCode",selttbank);//联行号
            bdquicJson.put("bankName",cardInfo.getBankName());//银行名称
            JSONObject jsonmodify = BDquickModify(bdquicJson);
            if(jsonmodify.get("code").equals("000")) {//成功
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

        JSONObject payJson = BDquickPay(jsonObject);
        String msg = payJson.getString("msg");
        Long states = 3L;//订单状态(0处理中，1成功，2失败，3未知)
        JSONObject resjson = new JSONObject();
        RestResult restResult = new RestResult();
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
     * 1.商户注册
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject BDquickRegist(JSONObject jsonObject) throws Exception {
        /**
         * 公共参数
         * url：请求地址
         * key；商户密钥
         * merchantNo：商户号
         */
        String url = "http://"+reqIp+"/beiduo/merchant/regist";
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("phone",jsonObject.getString("phone"));//结算卡预留手机号
        jsonData.put("settleCard",jsonObject.getString("settleCard"));//结算卡
        jsonData.put("realName",jsonObject.getString("realName"));//姓名
        jsonData.put("idCard",jsonObject.getString("idCard"));//身份证号
        jsonData.put("bankCode",jsonObject.getString("bankCode"));//联行号
        jsonData.put("orderCode",CommonUtils.getOrderNo());//唯一订单号
        jsonData.put("rate",jsonObject.getString("rate"));//费率
        jsonData.put("extraFee",jsonObject.getString("extraFee"));//单笔手续费
        jsonData.put("bankName",jsonObject.getString("bankName"));//银行名称
        jsonData.put("userId",jsonObject.getString("userId"));//用户ID
        jsonData.put("notifyUrl","");//通知地址

        logger.info("请求参数jsonData：{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        return commonApi.requestCommon(jsonObject);
    }

    /**
     * 2.商户修改
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject BDquickModify(JSONObject jsonObject) throws Exception {
        /**
         * 公共参数
         * url：请求地址
         * key；商户密钥
         * merchantNo：商户号
         */
        String url = "http://"+reqIp+"/beiduo/merchant/modify";
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("merchantCode",jsonObject.getString("merchantCode"));//商户编号
        jsonData.put("cardCode",jsonObject.getString("cardCode"));//结算卡号
        jsonData.put("cardPhone",jsonObject.getString("cardPhone"));//结算手机号
        jsonData.put("bankCode",jsonObject.getString("bankCode"));//联行号
        jsonData.put("rate",jsonObject.getString("rate"));//费率
        jsonData.put("extraFee",jsonObject.getString("extraFee"));//单笔手续费
        jsonData.put("bankName",jsonObject.getString("bankName"));//银行名称

        logger.info("请求参数jsonData：{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        return commonApi.requestCommon(jsonObject);
    }

    /**
     * 3.快捷支付
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject BDquickPay(JSONObject jsonObject) throws Exception {
        String url = "http://"+reqIp+"/beiduo/quick/pay";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("merchantCode",jsonObject.getString("merchantCode"));//商户编号
        jsonData.put("orderCode",jsonObject.getString("orderNo"));//唯一订单号
        jsonData.put("amount",jsonObject.getString("amount"));//金额
        jsonData.put("bankCode",jsonObject.getString("bankCode"));//银行简称
        jsonData.put("unionBankCode",jsonObject.getString("unionBankCode"));//交易卡联行号
        jsonData.put("cardCode",jsonObject.getString("cardCode"));//交易卡号
        jsonData.put("cardPhone",jsonObject.getString("cardPhone"));//交易卡手机号
        jsonData.put("validDate",jsonObject.getString("validDate"));//日期
        jsonData.put("cvv2",jsonObject.getString("cvv2"));//安全码
        jsonData.put("notifyUrl","http://"+serverip+"/app/bdquick/notify");//通知地址
        jsonData.put("frontUrl","");//前台显示地址
        jsonData.put("terminalIp","127.0.0.1");//终端 IP
        jsonData.put("province",jsonObject.getString("province"));//省
        jsonData.put("city",jsonObject.getString("city"));//市
        jsonData.put("realName",jsonObject.getString("realName"));//真实姓名
        jsonData.put("idCard",jsonObject.getString("idCard"));//身份证号
        jsonData.put("userId",jsonObject.getString("subuserId"));//用户 ID

        logger.info("请求参数jsonData：{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        return commonApi.requestCommon(jsonObject);
    }

    /**
     * 4.订单查询
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject BDquickPayQuery(JSONObject jsonObject) throws Exception {
        /**
         * 公共参数
         * url：请求地址
         * key；商户密钥
         * merchantNo：商户号
         */
        String url = "http://"+reqIp+"/beiduo/quick/pay/query";
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderCode"));//订单号

        logger.info("请求参数jsonData：{}",jsonData);

        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        return commonApi.requestCommon(jsonObject);
    }


}
