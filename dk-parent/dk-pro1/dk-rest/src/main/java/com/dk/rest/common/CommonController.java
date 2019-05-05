package com.dk.rest.common;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.AuthenUtils;
import com.common.utils.CommonUtils;
import com.common.utils.SendMsgUtils;
import com.dk.provider.basis.service.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/common")
public class CommonController {

    private Logger logger = LoggerFactory.getLogger(CommonController.class);

    /**
     * redis缓存service
     */
    @Resource
    private RedisCacheService redisCacheService;

    /**
     * 发送短信验证码 并且记录平台短信记录
     * @param jsonObject
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = {"/sendMsg"},method = RequestMethod.POST)
    public void sendMsg(@RequestBody JSONObject jsonObject) throws Exception{
        String phone = jsonObject.getString("phone");
        String verfiCode = CommonUtils.getRandom(6);

        //存redis  缓存时间5分钟
        redisCacheService.set(phone,verfiCode,5L);

        String res = SendMsgUtils.sendMsgPost(phone,verfiCode);
        logger.info("返回信息:{}",res);

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
