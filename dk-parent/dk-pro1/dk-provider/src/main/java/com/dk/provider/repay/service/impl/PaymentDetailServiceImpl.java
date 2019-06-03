package com.dk.provider.repay.service.impl;

import com.common.bean.RestResult;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.service.IPaymentDetailService;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;


@Service
public class PaymentDetailServiceImpl implements IPaymentDetailService {
    private PaymentDetailMapper paymentDetailMapper;


    @Override
    public List<PaymentDetail> query(Map map) throws Exception {
        return null;
    }

    @Override
    public int insert(PaymentDetail paymentDetail) throws Exception {
        return 0;
    }

    @Override
    public int update(PaymentDetail paymentDetail) throws Exception {
        return 0;
    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public PaymentDetail queryByid(Long id) {
        return null;
    }

    @Override
    public RestResult insertList(List<PaymentDetail> list) {
        RestResult restResult = new RestResult();
        paymentDetailMapper.insertList(list);
        return restResult;
    }
}
