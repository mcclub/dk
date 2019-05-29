package com.dk.rest.api.inter;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.IUserService;
import com.dk.rest.api.common.CommonApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @ClassName 请求上游快捷控制类(xinsheng大额 快捷K1) API
 * @Date 2019-03-27
 */
@Service
@PropertySource("classpath:application.properties")
public class XSquickPayApi {
    private Logger logger = LoggerFactory.getLogger(XSquickPayApi.class);

    @Resource
    private CommonApi commonApi;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private SubchannelService subchannelService;

    @Resource
    private ReceiveRecordService receiveRecordService;

    @Resource
    private RedisCacheService redisCacheService;

    /**上游密钥**/
    private String key ;
    /**上游商户号**/
    private String merchantNo;

    /**请求ip**/
    private String reqIp="api.channel.gumids.com";//生产

    @Value("${server.ip}")
    String serverip;



    /**
     * 1.消费申请
     * 请求消费，会返回平台订单号并发送验证码
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public RestResult XsQuickapply(JSONObject jsonObject) throws Exception {
        RestResult restResult = new RestResult();
        logger.info("XsQuickapply接收到jsonObject的参数为:{}",jsonObject);
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
        }else{
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到路由通道",new JSONObject());
        }
        /**
         * 查询交易卡信息
         */
        Map<String,Object> mapcardPay = new HashMap<>();
        mapcardPay.put("userId",userId);
        mapcardPay.put("cardCode",receCard);
        CardInfo info = userServiceImpl.queryCard(mapcardPay);
        if(info == null){
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"未找到绑定信用卡",new JSONObject());
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
         * 公共参数
         * url：请求地址
         * key；商户密钥
         * merchantNo：商户号
         */
        String url = "http://"+reqIp+"/xinsheng/new/quickPay/apply";
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderNo"));//唯一订单号
        jsonData.put("creditCard",jsonObject.getString("receCard"));//信用卡卡号
        jsonData.put("amount",jsonObject.getString("amount"));//订单金额
        jsonData.put("realName",info.getRealName());//用户真实姓名
        jsonData.put("creditValidDate",info.getValid());//信用卡有效期
        jsonData.put("cvv2",info.getCvv());//安全码
        jsonData.put("creditPhone",info.getPhone());//信用卡银行预留电话
        jsonData.put("creditBankName",info.getBankName());//信用卡所在银行名称
        jsonData.put("idCard",info.getIdentity());//身份证号
        jsonData.put("settleCard",cardInfo.getCardCode());//借记卡卡号
        jsonData.put("settlePhone",cardInfo.getPhone());//借记卡银行预留电话
        jsonData.put("notifyUrl","http://"+serverip+"/app/quick/xsnotify");//通知地址
        jsonData.put("userId","DH_"+info.getIdentity());//用户ID
        jsonData.put("province",jsonObject.getString("province"));//省
        jsonData.put("city",jsonObject.getString("city"));//市
        jsonData.put("rate",rate);//费率
        jsonData.put("extraFee",fee);//额外手续费
        if(jsonObject.containsKey("icpCode")){
            jsonData.put("icpCode",jsonObject.getString("icpCode"));//Icp 码
        }
        logger.info("请求参数jsonData:{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());
        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        JSONObject payJson = commonApi.requestCommon(jsonObject);

        String msg = payJson.getString("msg");
        Long states = 4L;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)
        JSONObject resjson = new JSONObject();

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

                resjson.put("orderNo",orderCode);
                resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）
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
     * 2.消费确认
     * 发送短信验证码到接口确认消费，交易结果会异步通知
     * @param jsonObject
     * @return
     */
    public RestResult XsQuickconfirm(JSONObject jsonObject) throws Exception{
        RestResult restResult = new RestResult();
        String url = "http://"+reqIp+"/xinsheng/new/quickPay/confirm";

        String receCard = jsonObject.getString("receCard");
        String upOrderCode = redisCacheService.get("XsK1up"+receCard);
        String orderCode = redisCacheService.get("XsK1od"+receCard);
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("upOrderCode",upOrderCode);//申请签约返回的平台订单号
        jsonData.put("orderCode",orderCode);//协议申请返回的短信流水号
        jsonData.put("smsCode",jsonObject.getString("smsCode"));//短信验证码
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        JSONObject payJson = commonApi.requestCommon(jsonObject);

        String msg = payJson.getString("msg");
        Long states = 2L;//订单状态(0处理中，1成功，2失败，3未知,4初始状态)
        JSONObject resjson = new JSONObject();

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

                resjson.put("orderNo",orderCode);
                resjson.put("resultType",1);//返回类型（1短信验证，2返回url地址,）
            }
            restResult = restResult.setCodeAndMsg(ResultEnume.SUCCESS,"交易成功",resjson);
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
     * 3.消费查询
     * 取消签订的协议，之后不可用于交易
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject XsQuickquery(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/quickPay/query";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderCode"));//唯一订单号

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
     * 4.手动重出款
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject XspayAgian(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/quickPay/again";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderCode"));//唯一订单号

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
     * 5 商户列表查询 new
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xsmerlist(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/merchant/list";


        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("province",jsonObject.getString("province"));//卡号
        jsonData.put("city",jsonObject.getString("city"));//业务协议号
        jsonData.put("mccCode",jsonObject.getString("mccCode"));//支付协议号

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
