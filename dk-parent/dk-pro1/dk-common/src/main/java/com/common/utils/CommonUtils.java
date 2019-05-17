package com.common.utils;

import com.common.bean.RestResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonUtils {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
    private static String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

    /**
     * 随机生成串数字字符串
     * @param count  位数
     * @return
     */
    public static String getRandom(int count) {
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i <= 5; i++) {
            sb.append(String.valueOf(r.nextInt(10)));
        }
        return sb.toString();
    }

    /**
     * 随机生成订单号
     * @return
     */
    public static String getOrderNo(){
        String orderNo = "DH"+date+ getRandom(4);
        return orderNo;
    }

    /**
     * 实体null转换为空字符串
     * @param e
     * @throws Exception
     */
    public static void reflect(Object e) throws Exception{
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            if(StringUtils.isEmpty(f.get(e))){
                f.set(e,"");
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Random random = new Random();
        int n = random.nextInt(2);//随机数
        System.out.println(n);
    }

}
