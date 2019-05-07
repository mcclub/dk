package com.dk.provider.plat.service;

import com.alibaba.fastjson.JSONObject;
import com.dk.provider.plat.entity.SmsRecord;

public interface SmsRecordService {

    int insertOne(JSONObject jsonObject) throws Exception;
}
