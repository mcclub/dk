package com.dk.provider.repay.service;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.repay.entity.PaymentDetail;

import java.util.List;
import java.util.Map;


public interface IPaymentDetailService extends BaseServiceI<PaymentDetail> {
    /**
     * 批量新增
     * @param list
     * @return
     */
    RestResult insertList (List<PaymentDetail> list);

    /**
     * 查询未执行的订单
     * @param map
     * @return
     */
    List<PaymentDetail> searchNotPerformed(Map map);

    /**
     * 修改状态并 添加返佣记录
     * @param jsonObject
     * @return
     */
    int updateDetailState(JSONObject jsonObject);

}
