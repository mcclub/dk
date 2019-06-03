package com.dk.rest.repay.controller;


import com.dk.provider.repay.service.IPaymentDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/paymentDetail")
public class PaymentDetailController {
    @Resource
    private IPaymentDetailService paymentDetailServiceImpl;


    @RequestMapping("/test")
    public void test (){
        Map<String,Object> map = new HashMap<>();
        paymentDetailServiceImpl.searchNotPerformed(map);
    }
}
