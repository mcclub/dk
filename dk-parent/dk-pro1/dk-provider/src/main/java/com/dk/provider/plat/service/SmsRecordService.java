package com.dk.provider.plat.service;

import com.alibaba.fastjson.JSONObject;
import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.plat.entity.SmsRecord;
import com.dk.provider.plat.entity.Subchannel;

import java.util.Map;

public interface SmsRecordService extends BaseServiceI<SmsRecord> {

    int insertOne(JSONObject jsonObject) throws Exception;


}
