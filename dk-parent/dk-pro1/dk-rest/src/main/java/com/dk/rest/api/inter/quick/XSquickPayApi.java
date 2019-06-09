package com.dk.rest.api.inter.quick;

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
    public JSONObject XsQuickapply(JSONObject jsonObject) throws Exception {
        /**
         * 公共参数
         * url：请求地址
         * key；商户密钥
         * merchantNo：商户号
         */
        String url = "http://"+reqIp+"/xinsheng/new/quickPay/apply";
        key = jsonObject.getString("key");
        merchantNo = jsonObject.getString("merchantNo");
        /**
         * jsonObject:业务参数
         */
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",jsonObject.getString("orderCode"));//唯一订单号
        jsonData.put("creditCard",jsonObject.getString("creditCard"));//信用卡卡号
        jsonData.put("amount",jsonObject.getString("amount"));//订单金额
        jsonData.put("realName",jsonObject.getString("realName"));//用户真实姓名
        jsonData.put("creditValidDate",jsonObject.getString("creditValidDate"));//信用卡有效期
        jsonData.put("cvv2",jsonObject.getString("cvv2"));//安全码
        jsonData.put("creditPhone",jsonObject.getString("creditPhone"));//信用卡银行预留电话
        jsonData.put("creditBankName",jsonObject.getString("creditBankName"));//信用卡所在银行名称
        jsonData.put("idCard",jsonObject.getString("idCard"));//身份证号
        jsonData.put("settleCard",jsonObject.getString("settleCard"));//借记卡卡号
        jsonData.put("settlePhone",jsonObject.getString("settlePhone"));//借记卡银行预留电话
        jsonData.put("notifyUrl","http://"+serverip+"/app/quick/xsnotify");//通知地址
        jsonData.put("userId",jsonObject.getString("subuserId"));//用户ID
        jsonData.put("province",jsonObject.getString("province"));//省
        jsonData.put("city",jsonObject.getString("city"));//市
        jsonData.put("rate",jsonObject.getString("rate"));//费率
        jsonData.put("extraFee",jsonObject.getString("extraFee"));//额外手续费
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

        return payJson;
    }

    /**
     * 2.消费确认
     * 发送短信验证码到接口确认消费，交易结果会异步通知
     * @param jsonObject
     * @return
     */
    public JSONObject XsQuickconfirm(JSONObject jsonObject) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/quickPay/confirm";
        key = jsonObject.getString("key");
        merchantNo = jsonObject.getString("merchantNo");

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

        return payJson;
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
