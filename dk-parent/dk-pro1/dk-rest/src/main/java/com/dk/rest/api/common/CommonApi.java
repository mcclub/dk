package com.dk.rest.api.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.HttpClients;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ascme
 * @ClassName 公共 API 方法
 * @Date 2019-04-07
 */
@Service
public class CommonApi {
    private Logger logger = LoggerFactory.getLogger(CommonApi.class);

    /**
     * 公共请求方式
     * @param jsonObject
     * @return
     */
    public JSONObject requestCommon(JSONObject jsonObject) {
        String merchantNo = jsonObject.get("merchantNo").toString();
        String url = jsonObject.get("url").toString();
        String key = jsonObject.get("key").toString();

        String data = "";
        if (jsonObject.containsKey("data")) {
            data = jsonObject.get("data").toString();
        }

        /**
         * sign:签名
         */
        String sign = null;
        if (!data.isEmpty()) {
            sign = DigestUtils.md5Hex(data + key);
        } else {
            sign = DigestUtils.md5Hex(key);
        }

        Map<String, String> map = new HashMap<>();
        map.put("merchantNo", merchantNo);
        if (!data.isEmpty()) {
            map.put("data", data);
        }
        map.put("sign", sign);
        logger.info("请求地址为:{}", url);
        logger.info("请求上游参数map为:{}", map);
        String res = HttpClients.sendPost(url, map);
        logger.info("上游返回参数为res:{}", res);
        JSONObject json = JSON.parseObject(res);
        return json;
    }
}
