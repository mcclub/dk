package com.common.utils;


import org.springframework.util.StringUtils;

import java.util.Random;

/**
 * 字符串处理公共类
 */
public class StringUtil {
    /**
     * 判断字符串为空
     * @param str
     * @return
     */
    public static boolean isEmpty (String str) {
        return (null == str || "".equals(str.trim()));
    }

    /**
     * 判断对象不为空
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !StringUtils.isEmpty(obj);
    }


    /**
     * 是否为邮箱地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        } else {
            String emReg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            return email.matches(emReg);
        }
    }

    /**
     * 是否为手机号码
     * @param phoneNo
     * @return
     */
    public static boolean isMobilePhone(String phoneNo) {
        if (isEmpty(phoneNo)) {
            return false;
        } else {
            String phReg = "^[1][345789]\\d{9}$";
            return phoneNo.matches(phReg);
        }
    }

    //随机获取用户名
    public static String getRandomName() {
         String val = "用户";
         Random random = new Random();
         //参数length，表示生成几位随机数
         for(int i = 0; i < 9; i++) {

                 String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
                 //输出字母还是数字
                 if( "char".equalsIgnoreCase(charOrNum) ) {
                         //输出是大写字母还是小写字母
                         //int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                        int temp = 97;
                        val += (char)(random.nextInt(26) + temp);
                     } else if( "num".equalsIgnoreCase(charOrNum) ) {
                         val += String.valueOf(random.nextInt(10));
                     }
             }
         return val;
     }

    public static void main(String[] args) {
        Object obj = new Object();
        System.out.println(StringUtil.isNotEmpty(obj));
    }
}
