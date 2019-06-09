package com.dk.rest.api.inter.quick;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dk.rest.api.inter.utils.DesUtil;
import com.dk.rest.api.inter.utils.HttpUtil;
import com.dk.rest.api.inter.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;

@Service
@PropertySource("classpath:application.properties")
public class TenfuTongReplaceApi {
    private Logger logger = LoggerFactory.getLogger(TenfuTongReplaceApi.class);

    /**上游密钥**/
    private String key ;
    /**上游商户号**/
    private String merchantNo;

    private String channelId;
    /**请求ip**/
    private String reqIp="epos.nocarddata.com";//生产

    @Value("${server.ip}")
    String serverip;


    /**
     * 1.用户注册
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject TFTregister(JSONObject jsonObject) throws Exception {

        String url = "http://"+reqIp+"/api/register";
        key = jsonObject.getString("key");
        merchantNo = jsonObject.getString("merchantNo");
        channelId = jsonObject.getString("channelId");
        /**
         * jsonObject:业务参数
         */
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("channel_id",channelId);//通道编号
        jsonData.put("name",jsonObject.getString("name"));//真实姓名
        jsonData.put("id_card",jsonObject.getString("id_card"));//身份证号
        jsonData.put("card_no",jsonObject.getString("card_no"));//结算银行卡号
        jsonData.put("mobile",jsonObject.getString("mobile"));//银行预留手机号
        jsonData.put("user_ip","127.0.0.1");//用户ip
        jsonData.put("timestamp",""+System.currentTimeMillis());//时间戳(13位)
        logger.info("请求参数jsonData:{}",jsonData);

        JSONObject jsonRes = requestPost(jsonData,url,"POST","des");
        return jsonRes;
    }


    /**
     * 3. 绑卡
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject TFTbind(JSONObject jsonObject) throws Exception {
        String url = "http://"+reqIp+"/api/bind";
        key = jsonObject.getString("key");
        merchantNo = jsonObject.getString("merchantNo");
        channelId = jsonObject.getString("channelId");
        /**
         * jsonObject:业务参数
         */
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("channel_id",channelId);//通道编号
        jsonData.put("user_id",jsonObject.getString("user_id"));//平台用户ID
        jsonData.put("card_no",jsonObject.getString("card_no"));//银行卡号
        jsonData.put("mobile",jsonObject.getString("mobile"));//银行预留手机号
        jsonData.put("user_ip","127.0.0.1");//操作人IP
        jsonData.put("expire_date",jsonObject.getString("expire_date"));//信用卡有效期,储蓄卡可不传
        jsonData.put("safe_code",jsonObject.getString("safe_code"));//信用卡安全码,储蓄卡可不传
        jsonData.put("return_url","");//同步返回地址
        jsonData.put("notice_url", "");//异步回调地址
        if(jsonObject.containsKey("sms_code")){
            jsonData.put("sms_code",jsonObject.getString("sms_code"));//短信验证码,某些通道绑卡时需要短信验证
        }
        if(jsonObject.containsKey("sms_id")){
            jsonData.put("sms_id",jsonObject.getString("sms_id"));//需要验证时,下发的数据,需要回传
        }
        jsonData.put("timestamp",""+System.currentTimeMillis());//时间戳(13位)
        logger.info("请求参数jsonData:{}",jsonData);

        JSONObject jsonRes = requestPost(jsonData,url,"POST","des");
        return jsonRes;
    }

    /**
     * 5. 消费
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject TFTpay(JSONObject jsonObject) throws Exception {

        String url = "http://"+reqIp+"/api/pay";
        key = jsonObject.getString("key");
        merchantNo = jsonObject.getString("merchantNo");
        channelId = jsonObject.getString("channelId");
        /**
         * jsonObject:业务参数
         */
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("channel_id",channelId);//通道编号
        jsonData.put("user_id",jsonObject.getString("user_id"));//平台账号ID
        jsonData.put("order_id",jsonObject.getString("order_id"));//订单号,不可重复
        jsonData.put("card_no",jsonObject.getString("card_no"));//银行卡号
        jsonData.put("amount",jsonObject.getString("amount"));//实际到账金额,单位分
        jsonData.put("fee",jsonObject.getString("fee"));//扣除手续费,单位分
        jsonData.put("notice_url","http://"+serverip+"/app/quick/tftpay/notify");//交易回调地址
        jsonData.put("user_ip","127.0.0.1");//用户登录IP
        if(jsonObject.containsKey("shop_id")){
            jsonData.put("shop_id",jsonObject.getString("shop_id"));//落地商铺ID(部份通知支持,可选)
        }
        jsonData.put("timestamp",""+System.currentTimeMillis());//时间戳(13位)
        logger.info("请求参数jsonData:{}",jsonData);

        JSONObject jsonRes = requestPost(jsonData,url,"POST","des");

        return jsonRes;
    }
    /**
     * 7. 代付
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject TFTproxypay(JSONObject jsonObject) throws Exception {
        String url = "http://"+reqIp+"/api/proxy_pay";
        key = jsonObject.getString("key");
        merchantNo = jsonObject.getString("merchantNo");
        channelId = jsonObject.getString("channelId");
        /**
         * jsonObject:业务参数
         */
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("channel_id",channelId);//通道编号
        jsonData.put("user_id",jsonObject.getString("user_id"));//平台账号ID
        jsonData.put("order_id",jsonObject.getString("order_id"));//订单号,不可重复
        jsonData.put("card_no",jsonObject.getString("card_no"));//银行卡号
        if(jsonObject.containsKey("mobile")){
            jsonData.put("mobile",jsonObject.getString("mobile"));//[可选]银行卡预留手机号码,如不传,则取注册时传的手机号
        }
        jsonData.put("amount",jsonObject.getString("amount"));//实际到账金额,单位分
        jsonData.put("fee",jsonObject.getString("fee"));//扣除手续费,单位分
        jsonData.put("notice_url","http://"+serverip+"/app/quick/tftpay/notify");//交易回调地址
        jsonData.put("user_ip","127.0.0.1");//用户登录IP
        jsonData.put("timestamp",""+System.currentTimeMillis());//时间戳(13位)
        logger.info("请求参数jsonData:{}",jsonData);

        JSONObject jsonRes = requestPost(jsonData,url,"POST","des");
        return jsonRes;
    }

    /**
     * 9. 余额查询
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public JSONObject TFTbalance(JSONObject jsonObject) throws Exception {
        String url = "http://"+reqIp+"/api/balance";

        /**
         * jsonObject:业务参数
         */
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("channel_id",channelId);//通道ID,如不传则返回所有通道的余额
        jsonData.put("user_id",jsonObject.getString("user_id"));//用户ID,可通过注册接口获得
        logger.info("请求参数jsonData:{}",jsonData);

        JSONObject jsonRes = requestPost(jsonData,url,"GET","des");
        return jsonRes;
    }

    /**
     * 10. 落地商户查询
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public String TFTshop(JSONObject jsonObject) throws Exception {


        String url = "http://"+reqIp+"/api/shop";

        /**
         * jsonObject:业务参数
         */
        HashMap<String, String> jsonData = new HashMap<String, String>();
        jsonData.put("channel_id",channelId);//通道ID,
        jsonData.put("province",jsonObject.getString("province"));//省份
        if(jsonObject.containsKey("city")){
            jsonData.put("city",jsonObject.getString("city"));//城市(可选)
        }
        jsonData.put("type_code",jsonObject.getString("type_code"));//行业大类
        if(jsonObject.containsKey("sub_type_code")){
            jsonData.put("sub_type_code",jsonObject.getString("sub_type_code"));//行业小类(可选)
        }

        HashMap jsonStr=jsonData;
        String js = HttpUtil.MapToStrSortWithOutKeys(jsonStr);

        String jsStr;
        if(js.substring(js.length()-1).equals("&")){
            jsStr = js + key;
        }else{
            jsStr = js + "&"+key;
        }

        logger.info("需要加密数据>>{}",jsStr);
        String si = MD5.md5(jsStr);
        logger.info("加密后数据>>{}" , si);
        jsonStr.put("sign",si);	  //签名

        org.json.JSONObject json = new org.json.JSONObject(jsonStr);
        String dateJson =  json.toString();
        logger.info("请求数据>>{}",dateJson);

        js = HttpUtil.MapToSt(jsonStr);
        if(js.substring(js.length()-1).equals("&")){
            js = js.substring(0,js.length()-1);
        }
        url =url +"?"+js;
        URLEncoder.encode(url,"utf-8");
        logger.info("请求上游地址>>{}",url);
        String result = HttpUtil.getWebContentByJsonGet(url, dateJson, false, null, 0,merchantNo,"des");

        logger.info("响应结果res：{}",result);

        if(result.indexOf("{") != -1){
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if(jsonResult != null){
                if(jsonResult.has("d")){
                    String keySub = key.substring(key.length()-8, key.length());
                    logger.info("截取的keySub>>>{}",keySub);
                    String d = (String) jsonResult.get("d");
                    String resultDecrypt = DesUtil.decrypt(d, keySub);
                    logger.info("响应结果>>{}",resultDecrypt);
                    return resultDecrypt;
                }else{
                    logger.info("响应结果>>不需要解析:{}" ,result);
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 公共请求
     * @param jsonStr
     * @param url
     * @return
     */
    public JSONObject requestPost(HashMap jsonStr, String url, String httpType, String xEncode) throws Exception{
        JSONObject jsonObject = new JSONObject();

        logger.info("请求原文串>>{}",jsonStr);
        String js = HttpUtil.MapToStrSortWithOutKeys(jsonStr);

        String jsStr;
        if(js.substring(js.length()-1).equals("&")){
            jsStr = js + key;
        }else{
            jsStr = js + "&"+key;
        }

        logger.info("需要加密数据>>{}",jsStr);
        String si = MD5.md5(jsStr);
        logger.info("加密后数据>>{}" , si);
        jsonStr.put("sign",si);	  //签名

        org.json.JSONObject json = new org.json.JSONObject(jsonStr);
        String dateJson =  json.toString();
        logger.info("请求数据>>{}",dateJson);

        String result = "";
        if(httpType.equals("GET")){
            js = HttpUtil.MapToSt(jsonStr);
            if(js.substring(js.length()-1).equals("&")){
                js = js.substring(0,js.length()-1);
            }
            url =url +"?"+js;
            URLEncoder.encode(url,"utf-8");
            logger.info("请求上游地址>>{}",url);
            result = HttpUtil.getWebContentByJsonGet(url, dateJson, false, null, 0,merchantNo,xEncode);
        }else if(httpType.equals("POST")){
            logger.info("请求上游地址>>{}",url);
            result =  HttpUtil.getWebContentByJsonPost(url, dateJson, false, null, 0,merchantNo,xEncode);
        }
        logger.info("响应结果res：{}",result);

        if(result.indexOf("{") != -1){
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if(jsonResult != null){
                if(jsonResult.has("d")){
                    String keySub = key.substring(key.length()-8, key.length());
                    logger.info("截取的keySub>>>{}",keySub);
                    String d = (String) jsonResult.get("d");
                    String resultDecrypt = DesUtil.decrypt(d, keySub);
                    logger.info("响应结果>>{}",resultDecrypt);
                    /*if(resultDecrypt.indexOf("[") >-1){
                        if(resultDecrypt.length() > 2){
                            String [] strArray2 = resultDecrypt.split(",");
                            List<String> strList = Arrays.asList(strArray2);
                            jsonObject.put("resultSucs",strList);
                            logger.info("结果jsonObject：{}",jsonObject);
                            return jsonObject;
                        }else{
                            jsonObject.put("resultSucs",resultDecrypt);
                            logger.info("结果jsonObject：{}",jsonObject);
                            return jsonObject;
                        }
                    }else{
                        return JSON.parseObject(resultDecrypt);
                    }*/
                    return JSON.parseObject(resultDecrypt);
                }else{
                    logger.info("响应结果>>不需要解析:{}" ,result);
                    return JSON.parseObject(result);
                }
            }
        }

        jsonObject.put("result",result);
        logger.info("结果jsonObject：{}",jsonObject);
        return jsonObject;
    }

    public void getresult() throws Exception{
        String key = "2e7e147e6b9ac5128bed4f91970ad48c";
        String result = "{\"d\":\"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\"}";
        JSONObject jsonObject = new JSONObject();
        if(result.indexOf("{") != -1){
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            logger.info("json:{}",jsonResult);
            if(jsonResult != null){
                if(jsonResult.has("d")){
                    String keySub = key.substring(key.length()-8, key.length());
                    logger.info("截取的keySub>>>{}",keySub);
                    String d = (String) jsonResult.get("d");
                    String resultDecrypt = DesUtil.decrypt(d, keySub);
                    logger.info("响应结果>>{}",resultDecrypt);
                    JSONArray array = JSONArray.parseArray(resultDecrypt);
                    //JSONArray array = JSON.parseArray(resultDecrypt.toString());
                    logger.info("结果array：{}",array);
                    /*if(resultDecrypt.indexOf("[") >-1){
                        if(resultDecrypt.length() > 2){
                            String [] strArray2 = resultDecrypt.split(",");
                            List<String> strList = Arrays.asList(strArray2);
                            jsonObject.put("resultSucs",strList);
                            logger.info("结果jsonObject：{}",jsonObject);
                        }else{
                            jsonObject.put("resultSucs",resultDecrypt);
                            logger.info("结果jsonObject：{}",jsonObject);
                        }
                    }else{
                    }*/
                    jsonObject.put("resultSucs",resultDecrypt);
                    logger.info("结果jsonObject：{}",jsonObject);
                }else{
                    logger.info("响应结果>>不需要解析:{}" ,result);
                }
            }
        }
    }
    public static void main(String[] args) throws Exception{
        TenfuTongReplaceApi api = new TenfuTongReplaceApi();
        api.getresult();
    }
}
