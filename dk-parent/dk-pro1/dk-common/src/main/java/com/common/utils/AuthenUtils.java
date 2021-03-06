package com.common.utils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 银行卡四要素鉴权
 */
public class AuthenUtils {
    private static String host = "https://yunyidata.market.alicloudapi.com";
    private static String path = "/bankAuthenticate4";
    private static String method = "POST";
    private static String appcode = "01ea2a2c362346d78836502c6c9c3e16";
    public static void main(String[] args) {
        String host = "https://yunyidata.market.alicloudapi.com";
        String path = "/bankAuthenticate4";
        String method = "POST";
        String appcode = "你自己的AppCode";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("ReturnBankInfo", "YES");
        bodys.put("cardNo", "62155811111111111");
        bodys.put("idNo", "340421199922225555");
        bodys.put("name", "张三");
        bodys.put("phoneNo", "13522221111");

        System.out.println();
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bankAuthen(Map<String, String> bodys){
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();

        bodys.put("ReturnBankInfo", "YES");
        /*Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("cardNo", "62155811111111111");
        bodys.put("idNo", "340421199922225555");
        bodys.put("name", "张三");
        bodys.put("phoneNo", "13522221111");*/
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{}";
    }

}
