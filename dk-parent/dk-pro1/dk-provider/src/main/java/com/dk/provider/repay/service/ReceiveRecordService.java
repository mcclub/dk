package com.dk.provider.repay.service;

import com.alibaba.fastjson.JSONObject;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.repay.entity.ReceiveRecord;

public interface ReceiveRecordService extends BaseServiceI<ReceiveRecord> {

    int insertOrder(JSONObject jsonObject) throws Exception;

    int updateStates(ReceiveRecord receiveRecord) throws Exception;
}
