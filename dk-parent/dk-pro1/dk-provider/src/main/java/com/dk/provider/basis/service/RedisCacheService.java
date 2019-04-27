package com.dk.provider.basis.service;

public interface RedisCacheService  {
    /**
     * 获取redis的值
     * @param var1 key键值
     * @return
     */
    String get(String var1);

    /**
     * 缓存redis的值
     * @param var1 key键值
     * @param var2 value键值
     * @param var3 缓存时间 分钟
     */
    void set(String var1, String var2, Long var3);

    /**
     * 删除redis的值
     * @param var1 key键值
     */
    void remove(String var1);

    /**
     * 是否存在key键值
     * @param var1 key键值
     * @return
     */
    boolean exist(String var1);
}
