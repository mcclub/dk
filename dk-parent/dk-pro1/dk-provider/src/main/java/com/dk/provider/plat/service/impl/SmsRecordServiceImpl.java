package com.dk.provider.plat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.utils.CommonUtils;
import com.common.utils.SendMsgUtils;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.SmsRecord;
import com.dk.provider.plat.mapper.SmsRecordMapper;
import com.dk.provider.plat.service.SmsRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("smsRecordService")
public class SmsRecordServiceImpl extends BaseServiceImpl<SmsRecord> implements SmsRecordService {

    private Logger logger = LoggerFactory.getLogger(SmsRecordServiceImpl.class);

    @Resource
    private RedisCacheService redisCacheService;

    @Resource
    private SmsRecordMapper smsRecordMapper;
    @Resource
    public void setSqlMapper (SmsRecordMapper smsRecordMapper)
    {
        super.setBaseMapper (smsRecordMapper);
    }
    /**
     * 发送短信
     * @param jsonObject
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOne(JSONObject jsonObject) throws Exception {
        String phone = jsonObject.getString("phone");
        String verfiCode = CommonUtils.getRandom(6);

        //存redis  缓存时间5分钟
        redisCacheService.set(phone,verfiCode,5L);

        String res = SendMsgUtils.sendMsgPost(phone,verfiCode);
        logger.info(phone + "发送短信返回信息:{}",res);
        JSONObject result = JSON.parseObject(res);

        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setPhone(phone);
        smsRecord.setContent("【乐享生态】您的验证码是"+verfiCode+"。如非本人操作，请忽略本短信");
        smsRecord.setDesc(result.getString("reason"));
        smsRecord.setOemId(jsonObject.getLong("oemId"));

        if(result.getInteger("error_code")==0){//发送成功
            smsRecord.setStates(1L);
            super.insert(smsRecord);
            return 1;
        }else{//发送失败
            smsRecord.setStates(2L);
            super.insert(smsRecord);
            return 0;
        }
    }

}
