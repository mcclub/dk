package com.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * 加密类
 */
public class EncryptionUtil {
    private static Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    /**
     * md5加密
     * @param source 加密串
     * @return
     */
    public static String md5 (String source) {
        StringBuffer sb = new StringBuffer(32);

        try {
            MessageDigest md 	= MessageDigest.getInstance("MD5");
            byte[] array 		= md.digest(source.getBytes("utf-8"));

            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toLowerCase().substring(1, 3));
            }
        } catch (Exception e) {
            logger.error("Can not encode the string '" + source + "' to MD5!", e);
            return null;
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(md5("123456"));
    }
}
