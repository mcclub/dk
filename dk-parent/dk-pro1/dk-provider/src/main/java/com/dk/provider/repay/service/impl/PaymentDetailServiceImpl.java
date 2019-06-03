package com.dk.provider.repay.service.impl;

import com.common.bean.RestResult;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.service.IPaymentDetailService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.*;


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


    /**
     * 根据时间对list元素排序
     * @param list
     */
    private static void ListSort(List<PaymentDetail> list) {
        Collections.sort(list, new Comparator<PaymentDetail>() {
             public int compare(PaymentDetail o1, PaymentDetail o2) {
                 try {
                         Date dt1 = o1.getActiveTime();
                         Date dt2 = o2.getActiveTime();
                         if (dt1.getTime() > dt2.getTime()) {
                                 return 1;
                             } else if (dt1.getTime() < dt2.getTime()) {
                                 return -1;
                             } else {
                                 return 0;
                             }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 return 0;
             }
         });
    }


    public static void main(String[] args) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PaymentDetail> list = new ArrayList<>();
        PaymentDetail a = new PaymentDetail();
        PaymentDetail b = new PaymentDetail();
        PaymentDetail c = new PaymentDetail();
        PaymentDetail d = new PaymentDetail();
        try {
            a.setActiveTime(DateUtils.parseDate("2019-05-29 08:12:33", format));
            b.setActiveTime(DateUtils.parseDate("2019-05-25 08:12:33", format));
            c.setActiveTime(DateUtils.parseDate("2019-05-31 08:12:33", format));
            d.setActiveTime(DateUtils.parseDate("2019-06-02 08:12:33", format));
        }catch (Exception e){

        }
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);

        for (PaymentDetail bean:list) {
            System.out.println("排序前："+bean.getActiveTime()+"\n");
        }

        ListSort(list);
        for (PaymentDetail bean:list) {
            System.out.println("排序后："+bean.getActiveTime()+"\n");
        }
    }
}
