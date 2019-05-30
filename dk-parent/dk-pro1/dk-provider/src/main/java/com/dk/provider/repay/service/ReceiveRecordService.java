package com.dk.provider.repay.service;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.repay.entity.ReceiveRecord;

import java.util.Map;

public interface ReceiveRecordService extends BaseServiceI<ReceiveRecord> {

    int insertOrder(JSONObject jsonObject) throws Exception;

    int updateStates(ReceiveRecord receiveRecord) throws Exception;

    RestResult pageHistory (Map map , Pageable pageable);
}
