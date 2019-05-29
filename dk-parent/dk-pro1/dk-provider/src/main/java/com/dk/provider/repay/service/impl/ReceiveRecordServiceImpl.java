package com.dk.provider.repay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.utils.CommonUtils;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.mapper.ReceiveRecordMapper;
import com.dk.provider.repay.service.ReceiveRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("receiveRecordService")
public class ReceiveRecordServiceImpl extends BaseServiceImpl<ReceiveRecord> implements ReceiveRecordService {

    private Logger logger = LoggerFactory.getLogger(ReceiveRecordServiceImpl.class);
    @Resource
    private ReceiveRecordMapper receiveRecordMapper;
    @Resource
    public void setSqlMapper (ReceiveRecordMapper receiveRecordMapper)
    {
        super.setBaseMapper (receiveRecordMapper);
    }

    @Resource
    private RouteInfoService routeInfoService;


    /**
     * 新增快捷订单 关联用户、费率信息
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrder(JSONObject jsonObject) throws Exception {
        Long userId = jsonObject.getLong("userId");//平台用户id
        String routId = jsonObject.getString("routId");//大类通道id

        /**
         * 根据用户id查询费率单笔手续费 ,到账储蓄卡
         */
        Map<String ,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("routId",routId);




        /**
         * 自动生成订单号
         */
        String orderNo = CommonUtils.getOrderNo();
        ReceiveRecord receiveRecord = new ReceiveRecord();
        receiveRecord.setOrderNo(orderNo);
        receiveRecord.setUserId(userId.toString());


        return receiveRecordMapper.insert(receiveRecord);
    }

    /**
     * 修改订单状态和描述
     * @param receiveRecord
     * @return
     * @throws Exception
     */
    @Override
    public int updateStates(ReceiveRecord receiveRecord) throws Exception {
        return receiveRecordMapper.updateStates(receiveRecord);
    }
}
