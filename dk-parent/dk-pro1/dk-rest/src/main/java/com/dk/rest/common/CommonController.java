package com.dk.rest.common;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.controller.BaseController;
import com.common.utils.AuthenUtils;
import com.common.utils.CommonUtils;
import com.common.utils.SendMsgUtils;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.plat.entity.SmsRecord;
import com.dk.provider.plat.service.SmsRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/comm")
public class CommonController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * redis缓存service
     */
    @Resource
    private RedisCacheService redisCacheService;
    /**
     * 短信记录
     */
    @Resource
    private SmsRecordService smsRecordService;

    /**
     * 发送短信验证码 并且记录平台短信记录
     * @param jsonObject
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = {"/sendMsg"},method = RequestMethod.POST)
    public RestResult sendMsg(@RequestBody JSONObject jsonObject) throws Exception{
        String method = "queryroute";
        logger.info("进入CommonController的"+method+"方法,参数为:{}",jsonObject);
        try{
            if(jsonObject.containsKey("phone")&&jsonObject.get("phone") !=null){

            }
            int ismsg = smsRecordService.insertOne(jsonObject);

            if(ismsg==1){//发送成功
                return getRestResult(ResultEnume.SUCCESS,"短信发送成功",null);
            }else{//发送失败
                return getRestResult(ResultEnume.FAIL,"短信发送失败",null);
            }
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 银行四要素信息鉴权(姓名,银行卡,身份证,手机号)
     * @param jsonObject
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = {"/bankAuthen"},method = RequestMethod.POST)
    public void bankAuthen(@RequestBody JSONObject jsonObject) throws Exception{

        Map<String,String> map = new HashMap<>();
        String res = AuthenUtils.bankAuthen(map);
        logger.info("返回信息:{}",res);
    }
}
