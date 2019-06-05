package com.dk.provider.repay.api;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.classI.entity.ClassRate;
import com.dk.provider.classI.service.ClassRateService;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.RouteUser;
import com.dk.provider.plat.entity.SubUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.entity.RepayPlan;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.service.IPaymentDetailService;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.IUserService;
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

    @Resource
    private RouteInfoService routeInfoService;

    @Resource
    private IPaymentDetailService paymentDetailServiceImpl;

    @Resource
    private PaymentDetailMapper paymentDetailMapper;


    /**上游密钥**/
    private String key ;
    /**上游商户号**/
    private String merchantNo;
    /**请求ip**/
    private String reqIp="api.channel.gumids.com";//生产

    @Value("${server.ip}")
    String serverip;




    /**
     * 4.收款请求
     * 收款接口，交易结果以异步为主
     * @param paymentDetail
     * @param subchannel
     * @return
     * @throws Exception
     */
    public JSONObject Xsreceiptapply(PaymentDetail paymentDetail, RepayPlan repayPlan,Subchannel subchannel) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/receipt/apply";
        if(subchannel != null){//小类通道信息
            merchantNo = subchannel.getMerNo();
            key = subchannel.getMerKey();
        }
        /**
         * 根据用户id查询协议等信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("userId",paymentDetail.getUserId());
        map.put("subId",repayPlan.getSubId());
        map.put("cardNo",repayPlan.getCardCode());
        SubUser subUser = subchannelService.querySubuser(map);
        String businessCode = "";
        String payCode = "";
        if(subUser != null){
            businessCode = subUser.getMerId();
            payCode = subUser.getMerKey();
        }
        /**
         * 根据大类通道id查询费率信息
         */
        Map<String ,Object> mapr = new HashMap<>();
        mapr.put("userId",paymentDetail.getUserId());
        mapr.put("routId",repayPlan.getRoutId());
        List<RouteInfo> routeInfoList = routeInfoService.findUserrate(mapr);
        String rate = "";
        if(routeInfoList != null){
            rate = routeInfoList.get(0).getRate();
        }

        /**
         * jsonObject:业务参数
         */
        String orderNode = CommonUtils.getOrderNo();
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",orderNode);//唯一订单号
        jsonData.put("businessCode",businessCode);//业务协议号
        jsonData.put("payCode",payCode);//支付协议号
        jsonData.put("notifyUrl","http://"+serverip+"/app/replace/xsnotify");//通知地址
        jsonData.put("userId",subUser.getSubuserId());//用户 ID
        jsonData.put("amount",paymentDetail.getAmount());//金额
        jsonData.put("province",repayPlan.getPlanArea());//省
        jsonData.put("city",repayPlan.getPlanCity());//市
        jsonData.put("rate",rate);//费率
        /*if(jsonObject.containsKey("icpCode")){
            jsonData.put("icpCode",jsonObject.getString("icpCode"));//Icp 码
        }*/


        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        JSONObject resjson = commonApi.requestCommon(jsonObject);

        /**
         * 根据计划id 修改该笔详情的订单号
         */
        PaymentDetail paymentDetail1 = new PaymentDetail();
        paymentDetail1.setId(paymentDetail.getId());
        paymentDetail1.setOrderNo(orderNode);
        paymentDetailMapper.update(paymentDetail1);


        return null;
    }



    /**
     * 6.付款接口
     * 执行还款，需要传收款的平台订单号
     * @param subchannel
     * @return
     * @throws Exception
     */
    public JSONObject Xspay(PaymentDetail paymentDetail, RepayPlan repayPlan,Subchannel subchannel) throws Exception{
        String url = "http://"+reqIp+"/xinsheng/new/pay";

        if(subchannel != null){//小类通道信息
            merchantNo = subchannel.getMerNo();
            key = subchannel.getMerKey();
        }

        /**
         * 根据用户id查询协议等信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("userId",paymentDetail.getUserId());
        map.put("subId",repayPlan.getSubId());
        map.put("cardNo",repayPlan.getCardCode());
        SubUser subUser = subchannelService.querySubuser(map);
        String subUserId = "";
        if(subUser != null){
            subUserId = subUser.getSubuserId();
        }
        /**
         * 根据大类通道id查询费率信息
         */
        Map<String ,Object> map1 = new HashMap<>();
        map1.put("userId",paymentDetail.getUserId());
        map1.put("routId",repayPlan.getRoutId());
        RouteUser routeUser = routeInfoService.queryUserRout(map1);
        String fee = "";
        if(routeUser != null){
            fee = routeUser.getFee();
        }
        /**
         * jsonObject:业务参数
         */
        String orderNode = CommonUtils.getOrderNo();
        JSONObject jsonData = new JSONObject();
        jsonData.put("orderCode",orderNode);//唯一订单号
        jsonData.put("amount",paymentDetail.getAmount());//金额
        jsonData.put("extraFee",fee);//单笔手续费（从付款金额扣）
        jsonData.put("realName",routeUser.getRealName());//收款人姓名
        jsonData.put("cardCode",repayPlan.getCardCode());//收款人卡号
        jsonData.put("userId",subUserId);//用户 ID
        jsonData.put("notifyUrl","http://"+serverip+"/app/replace/xspaynotify");//通知地址
        logger.info("请求参数jsonData：{}",jsonData);
        /**
         * data:业务参数base64后的字符串
         */
        String data =  Base64.getEncoder().encodeToString(jsonData.toString().getBytes());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url",url);
        jsonObject.put("key",key);
        jsonObject.put("merchantNo",merchantNo);
        jsonObject.put("data",data);
        JSONObject resjson = commonApi.requestCommon(jsonObject);

        /**
         * 根据计划id 修改该笔详情的订单号
         */
        PaymentDetail paymentDetail1 = new PaymentDetail();
        paymentDetail1.setId(paymentDetail.getId());
        paymentDetail1.setOrderNo(orderNode);
        paymentDetailMapper.update(paymentDetail1);

        return null;
    }


}
