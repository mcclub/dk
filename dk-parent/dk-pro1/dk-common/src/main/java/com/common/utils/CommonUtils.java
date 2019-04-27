package com.common.utils;

import java.util.Random;

public class CommonUtils {

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

}
