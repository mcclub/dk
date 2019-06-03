package com.dk.provider.repay.service;

import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.repay.entity.PaymentDetail;

import java.util.List;


public interface IPaymentDetailService extends BaseServiceI<PaymentDetail> {
    /**
     * 批量新增
     * @param list
     * @return
     */
    RestResult insertList (List<PaymentDetail> list);
}
