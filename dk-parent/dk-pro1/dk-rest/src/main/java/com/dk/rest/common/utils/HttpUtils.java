package com.dk.rest.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
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
            conn.setRequestProperty("Charset", "UTF-8");
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

            System.out.println("输出："+conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应    设置接收格式
            System.out.println(conn.getResponseCode());
            System.out.println("conn:"+conn.getResponseMessage());
            System.out.println("sdass:"+conn.getInputStream());

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
     * JSON 发送 Post请求
     * @param url
     * @param param
     * @return String
     */
    public static String sendJsonPost(String url,String param,String cookie){
        //CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        DefaultHttpClient httpclient = new DefaultHttpClient(new PoolingClientConnectionManager());
        HttpPost post = new HttpPost(url);

        CookieStore cookieStore = httpclient.getCookieStore();//获取cookies
        httpclient.setCookieStore(cookieStore);//设置cookies

        try {
            StringEntity s = new StringEntity(param ,"utf-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType

            post.setEntity(s);
            HttpResponse res = httpclient.execute(post);

            HttpEntity entity = res.getEntity();
            String result = EntityUtils.toString(res.getEntity());// 返回json格式：

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void getInter(){
        String urlPost = "http://a1.go2yd.com";
        String cookie = "JSESSIONID=H-nryHhWJiGC9UG0wg4rcQ";
        //1.获取登录用户信息 form
        /*String url="http://a1.go2yd.com/Website/user/get-info";
        String param="cv=4.9.4.1&os=26&appid=yidian&distribution=app.hicloud.com&brand=HUAWEI&version=022700&androidId=6a67aa44d799bce1566039e25db9d4c1&platform=1&reqid=dqhkdnih_1555151063549_1632&net=wifi&rr=OpenSSLSocketImpl.java_355_dqhkdnih_1555151063312_1632";
        */

        //2. application/json
        /*String url="http://a1.go2yd.com/Website/channel/news-list-for-best-channel";
        String param="amazing_comments=true&eventid=83054986739611c9b-6f0f-4943-8fe5-026210b3f93c&os=26&cstart=0&signature=evr1tY90qFyLck27RSfAd2V7hStwbSS8pIdtlSNhL9RD5wEVsqV1bNR_o2y2AZ9NvybntHDrtNpEKIOdpIQKF_z69KcRbv-uNHMWzQUty1DYKGCoEBnSNfSMOg2GeLEwQteqvQ4zXz4cdX0TzmMVZzAyi3kyQ9rMT9IVzxbERkk&infinite=true&searchentry=channel_navibar&group_fromid=g181&refresh=2&collection_num=0&distribution=app.hicloud.com&version=022700&platform=1&ad_version=010964&reqid=dqhkdnih_1555140102052_1594&cv=4.9.4.2&cend=30&appid=yidian&fields=docid&fields=date&fields=image&fields=image_urls&fields=like&fields=source&fields=title&fields=url&fields=comment_count&fields=up&fields=down&push_refresh=0&brand=HUAWEI&androidId=6a67aa44d799bce1566039e25db9d4c1&net=wifi&rr=OpenSSLSocketImpl.java_355_dqhkdnih_1555140101817_1594";
        */

        /*String url=urlPost + "/Website/contents/content";
        String param="userInfo=830549867&related_wemedia=true&related_docs=false&os=26&docid=0Ljm33Av,0LjvEJ0t,0LjasWA9,0LjI9ES1,0LjgSSUZ,0Lk9Fbkw&vertical_card=true&distribution=app.hicloud.com&deviceId=9VvlpKCLSRsHxHWfa9CtOg%3D%3D&version=022700&platform=1&reqid=dqhkdnih_1555408372853_2457&cv=4.9.4.2&related_navigations=true&appid=yidian&bottom_channels=true&brand=HUAWEI&androidId=6a67aa44d799bce1566039e25db9d4c1&net=wifi&rr=OpenSSLSocketImpl.java_355_dqhkdnih_1555408372420_2457";
*/
        /*String url =urlPost + "/Website/channel/recommend-channel";
        String param = "os=26&group_fromid=g181&distribution=app.hicloud.com&version=022700&platform=1&reqid=dqhkdnih_1555411455541_2476&cv=4.9.4.2&group_id=101924087307&appid=yidian&position=feed_head&channel_id=101924087307&brand=HUAWEI&androidId=6a67aa44d799bce1566039e25db9d4c1&net=wifi&rr=OpenSSLSocketImpl.java_355_dqhkdnih_1555411455295_2476";
        */

        String url = urlPost + "/Website/channel/news-list-for-channel";
        String param = "eventid=83054986776110c0c-d86c-499c-baf2-7f25af7579c8&os=26&cstart=0&signature=EOrMEBK6pBo_Exlx__nwvcfs2QAYcmEC2oGN7VrnKxtj6nhBfPpAyuGOOC3No-zd0MqjZDwCbexvSHvJ6CHCUpaBQe4bEJz686iOxU-XVi33p2jezihRnuG-5szD9dAXIdJfbqz4C93iVwigvlytifrpePlLcTLgisoNK1DDDs8&infinite=true&searchentry=channel_navibar&refresh=1&group_fromid=g181&distribution=app.hicloud.com&version=022700&platform=1&ad_version=010964&reqid=dqhkdnih_1555414052046_2477&cv=4.9.4.2&cend=30&appid=yidian&switch_local=false&fields=docid&fields=date&fields=image&fields=image_urls&fields=like&fields=source&fields=title&fields=url&fields=comment_count&fields=up&fields=down&channel_id=30619194907&brand=HUAWEI&androidId=6a67aa44d799bce1566039e25db9d4c1&last_docid=V_02cn2bNj&net=wifi&rr=OpenSSLSocketImpl.java_355_dqhkdnih_1555414051696_2477";

        url = url+"?"+param;

        String paramJson = "{\"clientInfo\":{\"deviceInfo\":{\"model\":\"VKY-AL00\",\"device\":\"HWVKY\",\"androidVersion\":\"8.0.0\",\"screenWidth\":1080,\"screenHeight\":1920,\"ppi\":480,\"brand\":\"HUAWEI\",\"manufacturer\":\"HUAWEI\",\"UA\":\"Dalvik\\/2.1.0 (Linux; U; Android 8.0.0; VKY-AL00 Build\\/HUAWEIVKY-AL00)\"},\"userInfo\":{\"imei\":\"7ad9170b5c1d4b3e49cfe546eb3e192b\",\"mac\":\"54:25:EA:65:20:01\",\"language\":\"zh\",\"country\":\"CN\",\"serviceProvider\":\"中国电信\",\"appVersion\":\"4.9.4.2\",\"androidId\":\"a11d9fe476f30d8c\",\"region\":\"%E6%B9%96%E5%8D%97%E7%9C%81%2C%E9%95%BF%E6%B2%99%E5%B8%82%2C%E5%BC%80%E7%A6%8F%E5%8C%BA%2C%E6%B9%96%E5%8D%97%E7%9C%81%E9%95%BF%E6%B2%99%E5%B8%82%E5%BC%80%E7%A6%8F%E5%8C%BA%E6%99%B4%E5%B2%9A%E8%B7%AF%E9%9D%A0%E8%BF%91%E5%8C%97%E8%BE%B0%E4%B8%89%E8%A7%92%E6%B4%B2%28%E5%9C%B0%E9%93%81%E7%AB%99%29\",\"cityCode\":\"0731\",\"adCode\":\"430105\",\"GPS\":\"28.2368495,112.9837459\",\"businessarea\":[],\"AOI\":[{\"id\":\"B0FFHMO50L\",\"name\":\"Hipark凤凰海购物公园\",\"location\":\"28.236787,112.982945\",\"area\":26471.82}]}}}";

        JSONObject json = JSON.parseObject(paramJson);
        System.out.println(json);
        System.out.println(url);
        //doStrPost(url,json);
        getInfo(url,paramJson,cookie,"form");
    }

    public static void getInfo(String url,String param,String cookie,String reqType){
        if(reqType.contains("json")){
            String res = sendJsonPost(url,param,cookie);
            System.out.println(res);
        }else if(reqType.contains("form")){
            String res = sendPost(url,param,cookie);
            System.out.println(res);

            JSONObject resjson = JSON.parseObject(res);
            JSONArray array = resjson.getJSONArray("result");
            System.out.println("array:--"+array);
            System.out.println();
            for(int i = 1; i < array.size(); i++){
                /*System.out.println("------"+en);
                JSONObject enjson = (JSONObject) en;*/
                JSONObject enjson = array.getJSONObject(i);

                String category =  enjson.getString("category");//分类
                String videoUrl = enjson.getString("video_url");//视频地址
                String title = enjson.getString("title");//视频标题
                JSONObject wemediaJson = enjson.getJSONObject("wemedia_info");
                System.out.println("wemediaJson---"+wemediaJson);
                String authorurl = wemediaJson.getString("image");//作者头像地址
                String authorname = wemediaJson.getString("name");//作者名称
                String userid = wemediaJson.getString("userid");//作者id
                String date = enjson.getString("date");//视频日期
                String playtimes = enjson.getString("duration");//播放时间

                System.out.println(category+"     "+videoUrl+"     "+title+"    "+authorurl+"     "+authorname+"      "+userid+"      "+date+"   "+playtimes);
                System.out.println();
            }
        }

    }

    public static void main(String[] args) {
        getInter();
    }

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

            HttpEntity entity = res.getEntity();
            String result = EntityUtils.toString(res.getEntity());// 返回json格式：
            System.out.println("doJsonPost返回参数："+result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
