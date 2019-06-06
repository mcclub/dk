package com.dk.rest.api.inter.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.DefaultProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtil {
	
	static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();	

  
    public static String getWebContentByJsonPost(String url, String objectString, boolean isProxy, String ip, int port
    		,String authorization,String xEncode)
            throws Exception {
        PostMethod post = new PostMethod(url);
        StringRequestEntity stringRequestEntity = new StringRequestEntity(objectString, "application/json", "UTF-8");
        post.setRequestEntity(stringRequestEntity);
        post.addRequestHeader("Content-type", "application/json; charset=" + "UTF-8");
        post.addRequestHeader("authorization", authorization);
        post.addRequestHeader("x-encode", xEncode);
        
        ProtocolSocketFactory fcty = new DefaultProtocolSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        HttpClient client = new HttpClient(connectionManager);
        if (isProxy) {
            client.getHostConfiguration().setProxy(ip, port);
            client.getParams().setAuthenticationPreemptive(true);
        }
        try {
            client.setTimeout(30000);
            int statusCode = client.executeMethod(post);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + post.getStatusLine());
                byte[] responseBody = post.getResponseBody();
                return new String(responseBody, "UTF-8");
            }
            byte[] responseBody = post.getResponseBody();
            return new String(responseBody, "UTF-8");
        } finally {
//            System.out.println("excute httpclient times #######" + (System.currentTimeMillis() - start) + "ms");
            post.releaseConnection();
        }
    }    
    
    public static String getWebContentByJsonGet(String url, String objectString, boolean isProxy, String ip, int port
    		,String authorization,String xEncode)
            throws Exception {
        GetMethod get = new GetMethod(url);      
        get.addRequestHeader("Content-type", "application/json; charset=" + "UTF-8");
        get.addRequestHeader("authorization", authorization);
        if(!StringUtils.isEmpty(xEncode)){
            get.addRequestHeader("x-encode", xEncode);
        }
        
        ProtocolSocketFactory fcty = new DefaultProtocolSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", fcty, 443));
        HttpClient client = new HttpClient(connectionManager);
        if (isProxy) {
            client.getHostConfiguration().setProxy(ip, port);
            client.getParams().setAuthenticationPreemptive(true);
        }
        try {
            client.setTimeout(30000);
            int statusCode = client.executeMethod(get);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + get.getStatusLine());
                byte[] responseBody = get.getResponseBody();
                return new String(responseBody, "UTF-8");
            }
            byte[] responseBody = get.getResponseBody();
            return new String(responseBody, "UTF-8");
        } finally {
            get.releaseConnection();
        }
    }  
             
    public static String MapToStrSortWithOutKeys(Map<String, String> requestParam, String... withOutKeys) throws UnsupportedEncodingException {
        StringBuffer strbuff = new StringBuffer();
        List<String> list = new ArrayList<String>(requestParam.keySet());
        Collections.sort(list);
        // 排除
        List<String> withOutList = null;
        if (null != withOutKeys && withOutKeys.length > 0) {
            withOutList = Arrays.asList(withOutKeys);
        }
        for (int i = 0; i < list.size(); i++) {
            String paramName = list.get(i);
            if (null != withOutList && withOutList.contains(paramName)) {
                continue;
            }
            String paramValue = requestParam.get(paramName);
            if(!StringUtils.isEmpty(paramValue)){
                strbuff.append(paramName).append("=").append(paramValue);

                if (i != list.size() - 1) {
                    strbuff.append("&");
                }
            }

            /*if(strbuff.substring(strbuff.length()-1,strbuff.length()).equals("&")){
                strbuff = strbuff.deleteCharAt(strbuff.length()-1);
            }*/

        }
        return strbuff.toString();
    }

    public static String MapToSt(Map<String, String> requestParam, String... withOutKeys) throws UnsupportedEncodingException {
        StringBuffer strbuff = new StringBuffer();
        List<String> list = new ArrayList<String>(requestParam.keySet());
        Collections.sort(list);
        // 排除
        List<String> withOutList = null;
        if (null != withOutKeys && withOutKeys.length > 0) {
            withOutList = Arrays.asList(withOutKeys);
        }
        for (int i = 0; i < list.size(); i++) {
            String paramName = list.get(i);
            if (null != withOutList && withOutList.contains(paramName)) {
                continue;
            }
            String paramValue = requestParam.get(paramName);
            if(!StringUtils.isEmpty(paramValue)){

                Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                Matcher m = p.matcher(paramValue);
                if (m.find()) {
                    paramValue = URLEncoder.encode(paramValue,"UTF-8");
                }

                strbuff.append(paramName).append("=").append(paramValue);

                if (i != list.size() - 1) {
                    strbuff.append("&");
                }
            }

        }
        return strbuff.toString();
    }

    public static void main(String[] args) throws Exception{
        /*HashMap<String,String > jsonStr = new HashMap<>();
        jsonStr.put("province","湖南省");
        jsonStr.put("city","长沙市");
        jsonStr.put("sign","89b15abebfe025e204709c2d4cc1142e");
        jsonStr.put("channel_id","22");
        String js = HttpUtil.MapToStrSortWithOutKeys(jsonStr);
        System.out.println(js);*/
        String key = "2e7e147e6b9ac5128bed4f91970ad48c";
        String keySub = key.substring(key.length()-8, key.length());
        String d = "D11t9YG0HgA=";
        String resultDecrypt = DesUtil.decrypt(d, keySub);
        System.out.println(resultDecrypt);


        JSONObject sjon = new JSONObject();
        sjon.put("1","1");

        List list1 = new ArrayList();
        list1.add("1");
        list1.add(sjon);
        System.out.println(list1);
        System.out.println(list1.size());
        System.out.println(list1.toString());

        String strString = "[{\"name\":\"长沙胜道体育用品有限公司长沙九龙仓店\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"2\",\"id\":\"846551059990001\"},{\"name\":\"长沙三亩地美术馆\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710010\"},{\"name\":\"长沙市望城区雷锋演艺有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710009\"},{\"name\":\"长沙山里人民族演艺有限责任公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710008\"},{\"name\":\"长沙炫典儿童戏剧演出有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710007\"},{\"name\":\"长沙尚雅文艺培训有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710006\"},{\"name\":\"长沙丹辰文化创意有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710005\"},{\"name\":\"长沙市望城区新康戏乡兰桂演艺有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710004\"},{\"name\":\"长沙田汉大剧院\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710003\"},{\"name\":\"长沙新华联铜官窑国际文化旅游开发有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710002\"},{\"name\":\"长沙市岳麓区老面臣餐馆\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120013\"},{\"name\":\"长沙锦庭餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120012\"},{\"name\":\"长沙市永景餐饮管理有限公司长沙分公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120011\"},{\"name\":\"长沙吉星红餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120010\"},{\"name\":\"长沙市芙蓉区君昊餐厅\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120009\"},{\"name\":\"长沙琳家餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120008\"},{\"name\":\"长沙饮食集团长沙火宫殿有限公司东塘分店\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120007\"},{\"name\":\"长沙悦胜铭餐饮管理有限公司长沙分公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120006\"},{\"name\":\"长沙市恩卓餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120005\"},{\"name\":\"长沙味域渔庄餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120004\"}]";
        if(strString.indexOf("[") >-1){
            if(strString.length() > 2){
                String strToArrayStr2 = "[{\"name\":\"长沙胜道体育用品有限公司长沙九龙仓店\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"2\",\"id\":\"846551059990001\"},{\"name\":\"长沙三亩地美术馆\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710010\"},{\"name\":\"长沙市望城区雷锋演艺有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710009\"},{\"name\":\"长沙山里人民族演艺有限责任公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710008\"},{\"name\":\"长沙炫典儿童戏剧演出有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710007\"},{\"name\":\"长沙尚雅文艺培训有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710006\"},{\"name\":\"长沙丹辰文化创意有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710005\"},{\"name\":\"长沙市望城区新康戏乡兰桂演艺有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710004\"},{\"name\":\"长沙田汉大剧院\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710003\"},{\"name\":\"长沙新华联铜官窑国际文化旅游开发有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551059710002\"},{\"name\":\"长沙市岳麓区老面臣餐馆\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120013\"},{\"name\":\"长沙锦庭餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120012\"},{\"name\":\"长沙市永景餐饮管理有限公司长沙分公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120011\"},{\"name\":\"长沙吉星红餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120010\"},{\"name\":\"长沙市芙蓉区君昊餐厅\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120009\"},{\"name\":\"长沙琳家餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120008\"},{\"name\":\"长沙饮食集团长沙火宫殿有限公司东塘分店\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120007\"},{\"name\":\"长沙悦胜铭餐饮管理有限公司长沙分公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120006\"},{\"name\":\"长沙市恩卓餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120005\"},{\"name\":\"长沙味域渔庄餐饮有限公司\",\"province\":\"湖南省\",\"city\":\"长沙市\",\"type_code\":\"1\",\"id\":\"846551058120004\"}]";

                String [] strArray2 = strToArrayStr2.split(",");
                List<String> strList = Arrays.asList(strArray2);
                System.out.println("遍历集合开始:"+strList);
            }else{
                System.out.println(strString);
            }
        }





        /*HashMap<String,String > jsonStr = new HashMap<>();
        jsonStr.put("province","湖南省");
        jsonStr.put("city","长沙市");
        jsonStr.put("sign","89b15abebfe025e204709c2d4cc1142e");
        jsonStr.put("channel_id","22");
        String js = HttpUtil.MapToSt(jsonStr);
        System.out.println(js);

        String str = "asd";
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            str = URLEncoder.encode(str,"UTF-8");
            System.out.println(1);
        }
        System.out.println(2);

        System.out.println(str);*/
    }
         
    
}
