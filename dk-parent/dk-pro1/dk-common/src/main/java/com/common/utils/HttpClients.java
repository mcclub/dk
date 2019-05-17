package com.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpClients {

    private static final Logger logger = LoggerFactory.getLogger(HttpClients.class);

    /**
     * 向指定URL发送POST方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param map
     *            请求Map参数，请求参数应该是 {"name1":"value1","name2":"value2"}的形式。
     * @param charset
     *             发送和接收的格式
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String,Object> map,String charset) {
        StringBuffer sb=new StringBuffer();
        //构建请求参数
        /*if(map!=null&&map.size()>0){
            for (Map.Entry<String, Object> e : map.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
        }*/
        JSONObject json =new JSONObject(map);//map转json

        return  sendPost(url,json.toString(),charset);
    }
    /**
     * json格式请求
     * @param url
     * @param jsonObject
     * @return
     */
    public static String sendPost(String url ,JSONObject jsonObject){
        return sendPost(url,jsonObject.toString(),"UTF-8");
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param charset
     *             发送和接收的格式
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param,String charset ) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String line;
        StringBuffer sb=new StringBuffer();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性 设置请求格式
            //conn.setRequestProperty("contentType", charset);
            //conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1");
            conn.setRequestProperty("Cookie", charset);
            //conn.setRequestProperty("Charset", "UTF-8");
            //设置超时时间
            conn.setConnectTimeout(30000);//毫秒
            conn.setReadTimeout(30000);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // post请求不应该使用cache
            conn.setUseCaches(false);

            //显式地设置为POST，默认为GET
            conn.setRequestMethod("POST");
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());

            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应    设置接收格式
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result=sb.toString();
        } catch (Exception e) {
            System.out.println("发送 POST请求出现异常!"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求  form表单提交
     * @param url
     * @param param
     * @return
     */
    public static String sendPost(String url, Map<String, String> param) {
        StringBuffer resultBuffer = null;
        // 构建请求参数
        StringBuffer sbParams = new StringBuffer();
        if (param != null && param.size() > 0) {
            for (Map.Entry<String, String> e : param.entrySet()) {
                sbParams.append(e.getKey());
                sbParams.append("=");
                sbParams.append(e.getValue());
                sbParams.append("&");
            }
        }
        URLConnection con = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            con = realUrl.openConnection();
            // 设置通用的请求属性
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            con.setDoOutput(true);
            con.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            osw = new OutputStreamWriter(con.getOutputStream());
            if (sbParams != null && sbParams.length() > 0) {
                // 发送请求参数
                osw.write(sbParams.substring(0, sbParams.length() - 1));
                // flush输出流的缓冲
                osw.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            resultBuffer = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
            String temp;
            while ((temp = br.readLine()) != null) {
                resultBuffer.append(temp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    osw = null;
                    throw new RuntimeException(e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                    throw new RuntimeException(e);
                }
            }
        }
        return resultBuffer.toString();
    }

    /**
     * JSON 发送 Post请求
     * @param url
     * @param json
     * @return JSONObject
     */
    public static com.alibaba.fastjson.JSONObject doJsonPost(String url,com.alibaba.fastjson.JSONObject json){
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        com.alibaba.fastjson.JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                logger.info("doJsonPost返回参数：{}",result);
                response = com.alibaba.fastjson.JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
    /**
     * JSON 发送 Post请求
     * @param url
     * @param json
     * @return String
     */
    public static String doStrPost(String url,com.alibaba.fastjson.JSONObject json){
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        com.alibaba.fastjson.JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString(),"utf-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);
            if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = res.getEntity();
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                logger.info("doJsonPost返回参数：{}",result);
                return result;
            }
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        //{orderId=201904111405233623630008, orderStatus=02, respMsg=交易成功, respCode=0000}
        String notifyUrl = "http://47.92.252.143/katong/gongyifu/quick/settleNotify";
        /*com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put("orderNo","201903062138123412sad");
        json.put("orderStatus","-1");
        json.put("errorCode","9009");
        json.put("errorMsg","未找到路由");
        System.out.println(doJsonPost(notifyUrl,json));*/
        //{orderId=D201904111405233623630008, orderStatus=02, respMsg=交易成功, respCode=0000}
        /*Map<String ,String > map = new HashMap<>();
        map.put("orderId","D201904111405233623630008");
        map.put("orderStatus","02");
        map.put("respMsg","交易成功");
        map.put("respCode","0000");
        System.out.println(sendPost(notifyUrl,map));*/

        /*String res = "200";
        com.alibaba.fastjson.JSONObject json = JSON.parseObject(res);
        System.out.println(json);*/

    }
}
