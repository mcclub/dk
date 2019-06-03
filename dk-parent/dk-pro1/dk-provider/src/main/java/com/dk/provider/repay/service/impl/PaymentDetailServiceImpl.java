package com.dk.provider.repay.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.service.IPaymentDetailService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.crypto.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class PaymentDetailServiceImpl extends BaseServiceImpl<PaymentDetail> implements IPaymentDetailService {
    private Logger logger = LoggerFactory.getLogger(PaymentDetailServiceImpl.class);
    @Resource
    private PaymentDetailMapper paymentDetailMapper;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");


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
        if (list != null && list.size() >0) {
            int num = paymentDetailMapper.insertList(list);
            if (num > 0) {
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"新增计划成功");
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"新增计划失败");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误，还款计划拆分失败");
        }
    }

    @Override
    public List<PaymentDetail> searchNotPerformed(Map map) {
        Map<String,Object> parm = new HashMap<>();
        String time = simpleDateFormat.format(new Date());
        List<Long> palantIdList = new ArrayList<>();//计划id List
        List<Long> detailIdList = new ArrayList<>();//详情id List
        parm.put("time",time);
        //查询即将执行的订单
        List<PaymentDetail> list = paymentDetailMapper.searchNotPerformed(parm);
        if (list != null && list.size() > 0) {
            for (PaymentDetail bean:list) {
                try {
                    if ((bean.getType()).equals(0)) {
                        //TODO 调用快捷接口
                        logger.info("调用快捷接口");
                    }
                    if ((bean.getType()).equals(1)) {
                        //TODO 调用代还接口
                        logger.info("调用代还接口");
                    }
                    palantIdList.add(bean.getPlanId());
                    detailIdList.add(bean.getId());
                } catch (Exception e) {
                    logger.info("错误信息="+e.getMessage());
                    logger.info("错误计划id="+bean.getPlanId()+"\t"+"错误详情id="+bean.getId());
                }
            }
            //更新计划以及详情状态
            int updatePantStatusNum = paymentDetailMapper.updatePantStatus(palantIdList);
            if (updatePantStatusNum > 0) {
                logger.info("计划订单状态更新成功");
            }

            //更新详情状态
            int updateDetailNum = paymentDetailMapper.updateDetailStatus(detailIdList);
            if (updateDetailNum > 0) {
                logger.info("详情订单状态更新成功");
            }




            for (Long plantId : palantIdList) {
                //查询计划下的详情是否执行完
                Map<String,Object> plantParm = new HashMap<>();
                plantParm.put("plantId",plantId);
                List<PaymentDetail> paymentDetailList = paymentDetailMapper.searchDetailIfFinish(plantParm);
                if (!(paymentDetailList != null && paymentDetailList.size()>0)) {
                    Map<String,Object> idParm = new HashMap<>();
                    idParm.put("id",plantId);
                    paymentDetailMapper.updatePlantFinish(idParm);
                }
            }


        }
        return null;
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
