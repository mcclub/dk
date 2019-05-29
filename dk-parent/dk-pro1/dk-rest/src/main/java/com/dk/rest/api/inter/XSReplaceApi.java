package com.dk.rest.api.inter;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.plat.entity.SubUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.SubchannelService;
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
import java.util.Map;

/**
 * @Author ascme
 * @ClassName 请求上游代还控制类(xinsheng大额) API
 * @Date 2019-03-18
 */
@Service
@PropertySource("classpath:application.properties")
public class XSReplaceApi {

    private Logger logger = LoggerFactory.getLogger(XSReplaceApi.class);

    @Resource
    private CommonApi commonApi;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private RedisCacheService redisCacheService;

    @Resource
    private SubchannelService subchannelService;

    /**上游密钥**/
    private String key ;
    /**上游商户号**/
    private String merchantNo;
    /**请求ip**/
    private String reqIp="api.channel.gumids.com";//生产

    @Value("${server.ip}")
    String serverip;

    private String notify = "";//扣款的异步回调地址

    private String payNotify = "";//付款的异步回调地址

    /**
     * 1.签约申请
     * 进行代扣需要先申请签约，签约会返回订单号，然后根据订单号调用签约确认接口确认签约。
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public RestResult Xsapply(JSONObject jsonObject, Subchannel subchannel) throws Exception{
        RestResult restResult = new RestResult();

        if(subchannel != null){//小类通道信息
            merchantNo = subchannel.getMerNo();
            key = subchannel.getMerKey();
        }
        /**
         * 根据用户id 和签约卡号查询绑卡信息
         */
        String userId = jsonObject.getString("userId");//用户id
        String cardCode = jsonObject.getString("cardCode");//签约卡号
        Map<String,Object> mapcardPay = new HashMap<>();
        mapcardPay.put("userId",userId);
        mapcardPay.put("cardCode",cardCode);
        CardInfo info = userServiceImpl.queryCard(mapcardPay);
        if(info == null){
            return  restResult.setCodeAndMsg(ResultEnume.FAIL,"签约卡信息未找到",new JSONObject());
        }
        /**
         * 公共参数
         * url：请求地址
         * key；商户密钥
         * merchantNo：商户号
         */
        String url = "http://"+reqIp+"/xinsheng/new/agreement/apply";

        String orderCode = CommonUtils.getOrderNo();
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode", orderCode);//唯一订单号
        jsonData.put("cardCode",cardCode);//信用卡卡号
        jsonData.put("realName",info.getRealName());//用户真实姓名
        jsonData.put("validDate",info.getValid());//有效期
        jsonData.put("cvv2",info.getCvv());//安全码
        jsonData.put("idCard",info.getIdentity());//身份证号
        jsonData.put("phone",info.getPhone());//电话号
        jsonData.put("userId","DH_"+info.getIdentity());//用户ID
        jsonData.put("bankName",info.getBankName());//银行名称

        logger.info("请求参数jsonData:{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);

        JSONObject json = commonApi.requestCommon(jsonObject);
        String msg = json.getString("msg");
        if(json != null){
            if("000".equals(json.get("code"))){//请求成功
                String dataStr = json.getString("data");//申请签约返回的平台订单号
                redisCacheService.set("XsDeupOrderNo"+cardCode,dataStr,2*60L);//缓存2个小时
                redisCacheService.set("XsDeorderCode"+cardCode,orderCode,2*60L);//缓存2个小时

                JSONObject jsonRes = new JSONObject();
                jsonRes.put("isSign","0");//1已签约，0未签约
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"签约申请提交成功,已发送短信验证码",jsonRes);
            }
        }

        return restResult.setCodeAndMsg(ResultEnume.FAIL,msg);
    }

    /**
     * 2.签约确认
     * 确认签约接口，验证通过后会返回业务协议号和支付协议号
     * @param jsonObject
     * @return
     */
    public RestResult Xsconfirm(JSONObject jsonObject,Subchannel subchannel) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/agreement/confirm";
        RestResult restResult = new RestResult();

        if(subchannel != null){//小类通道信息
            merchantNo = subchannel.getMerNo();
            key = subchannel.getMerKey();
        }

        String upOrderNo = redisCacheService.get("XsDeupOrderNo"+jsonObject.getString("cardCode"));//申请签约返回的平台订单号
        String orderNo = redisCacheService.get("XsDeorderCode"+jsonObject.getString("cardCode"));//上送订单号
        String userId = jsonObject.getString("userId");//用户id
        String cardCode = jsonObject.getString("cardCode");//签约卡号
        String smsCode = jsonObject.getString("smsCode");//短信验证码
        String subId = jsonObject.getString("subId");//小类通道id
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("upOrderNo",upOrderNo);//申请签约返回的平台订单号
        jsonData.put("orderNo",orderNo);//协议申请返回的短信流水号
        jsonData.put("smsCode",smsCode);//短信验证码
        logger.info("请求参数jsonData:{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        JSONObject json = commonApi.requestCommon(jsonObject);
        String msg = json.getString("msg");
        if(json != null){
            if("000".equals(json.get("code"))){//请求成功
                JSONObject dataJson= json.getJSONObject("data");
                /**
                 * 查询签约卡信息
                 */
                Map<String,Object> mapcardPay = new HashMap<>();
                mapcardPay.put("userId",userId);
                mapcardPay.put("cardCode",cardCode);
                CardInfo cardInfo = userServiceImpl.queryCard(mapcardPay);
                if(cardInfo != null){
                    /**
                     * 记录用户签约信息
                     */
                    SubUser subUser1 = new SubUser();
                    subUser1.setUserId(userId);//用户id
                    subUser1.setName(cardInfo.getRealName());//姓名
                    subUser1.setCardNo(cardInfo.getCardCode());//卡号
                    subUser1.setBankName(cardInfo.getBankName());//银行名称
                    subUser1.setPhone(cardInfo.getPhone());//手机号
                    subUser1.setIdentity(cardInfo.getIdentity());//身份证
                    subUser1.setSubuserId("DH_"+cardInfo.getIdentity());//小类通道用户id
                    subUser1.setSubId(Long.valueOf(subId));//小类通道id
                    /*subUser1.setRate(rate);//用户费率
                    subUser1.setFee(fee);//用户单笔手续费(元)*/
                    subUser1.setMerId(dataJson.getString("businessCode"));//业务协议号
                    subUser1.setMerKey(dataJson.getString("payCode"));//支付协议号
                    subchannelService.insertSubuser(subUser1);

                }

                /*String businessCode = dataJson.getString("businessCode");//业务协议号
                String payCode = dataJson.getString("payCode");//支付协议号
                redisCacheService.set("XsDeconfirm",dataJson);*/

                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"签约成功",new JSONObject());
            }
        }

        return restResult.setCodeAndMsg(ResultEnume.FAIL,msg);
    }

    /**
     * 3.取消签约
     * 取消签订的协议，之后不可用于交易
     * @param jsonObject
     * @return
     */
    public JSONObject Xscannel(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/agreement/cancel";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderNo",jsonObject.getString("orderNo"));//唯一订单号
        jsonData.put("businessCode",jsonObject.getString("businessCode"));//业务协议号
        jsonData.put("payCode",jsonObject.getString("payCode"));//支付协议号
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
     * 4.收款请求
     * 收款接口，交易结果以异步为主
     * @param jsonObject
     * @return
     */
    public JSONObject Xsreceiptapply(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/receipt/apply";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderCode"));//唯一订单号
        jsonData.put("businessCode",jsonObject.getString("businessCode"));//业务协议号
        jsonData.put("payCode",jsonObject.getString("payCode"));//支付协议号
        jsonData.put("notifyUrl",notify);//通知地址
        jsonData.put("userId",jsonObject.getString("userId"));//用户 ID
        jsonData.put("amount",jsonObject.getString("amount"));//金额
        jsonData.put("province",jsonObject.getString("province"));//省
        jsonData.put("city",jsonObject.getString("city"));//市
        jsonData.put("rate",jsonObject.getString("rate"));//费率
        if(jsonObject.containsKey("icpCode")){
            jsonData.put("icpCode",jsonObject.getString("icpCode"));//Icp 码
        }


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
     * 5.收款查询
     * 查询收款订单
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xsreceiptquery(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/receipt/query";

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
     * 6.付款接口
     * 执行还款，需要传收款的平台订单号
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xspay(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/pay";


        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderCode"));//唯一订单号
        jsonData.put("amount",jsonObject.getString("amount"));//金额
        jsonData.put("extraFee",jsonObject.getString("extraFee"));//单笔手续费（从付款金额扣）
        jsonData.put("realName",jsonObject.getString("realName"));//收款人姓名
        jsonData.put("cardCode",jsonObject.getString("cardCode"));//收款人卡号
        jsonData.put("userId",jsonObject.getString("userId"));//用户 ID
        jsonData.put("notifyUrl",payNotify);//通知地址
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
     * 7.付款查询
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xspayquery(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/pay/query";


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
     * 8 用户余额查询
     * 订单查询接口
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xsbalancequery(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/user/balance/query";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("userId",jsonObject.getString("userId"));//用户 ID

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
     * 9 订单批量查询
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xsquerytime(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/order/query/time";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("beginTime",jsonObject.getString("beginTime"));//开始时间
        jsonData.put("endTime",jsonObject.getString("endTime"));//结束时间
        jsonData.put("type",jsonObject.getString("type"));//订单类型
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
     * 10 商户分润金额查询
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xsprofitquery(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/profit/query";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("merchantNo",jsonObject.getString("merchantNo"));//商户编号
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
     * 11 卡余额查询 new
     * 订单查询接口
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject XsCardquery(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/bank/card/balance/query";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("cardCode",jsonObject.getString("cardCode"));//卡号

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
     * 12 签约验证 new
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject Xsvalid(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/agreement/valid";

        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("cardCode",jsonObject.getString("cardCode"));//卡号
        jsonData.put("businessCode",jsonObject.getString("businessCode"));//业务协议号
        jsonData.put("payCode",jsonObject.getString("payCode"));//支付协议号

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
     * 13 商户列表查询 new
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
