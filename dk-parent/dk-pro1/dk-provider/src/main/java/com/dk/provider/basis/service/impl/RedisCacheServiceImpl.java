package com.dk.provider.basis.service.impl;

import com.dk.provider.basis.service.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service("redisCacheService")
public class RedisCacheServiceImpl implements RedisCacheService {

    private Logger logger = LoggerFactory.getLogger(RedisCacheServiceImpl.class);

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public String get(String key) {
        String obj = redisTemplate.opsForValue().get(key);
        if(obj !=null){
            return obj;
        }
        return null;
    }

    @Override
    public void set(String var1, String var2) {
        if(var2!=null){
            redisTemplate.opsForValue().set(var1, var2);
        }
    }

    @Override
    public void set(String var1, String var2, Long var3) {
        if(var2!=null){
            redisTemplate.opsForValue().set(var1, var2, var3, TimeUnit.MINUTES);
        }
    }

    @Override
    public void remove(String var1) {
        if(var1 != null){
            redisTemplate.opsForValue().getOperations().delete(var1);
        }
    }

    @Override
    public boolean exist(String var1) {
        if(var1 !=null){
            return redisTemplate.hasKey(var1);
        }
        return false;
    }



}
