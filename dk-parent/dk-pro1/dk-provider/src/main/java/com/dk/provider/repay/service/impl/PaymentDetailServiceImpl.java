package com.dk.provider.repay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.mapper.RouteInfoMapper;
import com.dk.provider.plat.mapper.SubchannelMapper;
import com.dk.provider.rake.entity.RakeRecord;
import com.dk.provider.rake.mapper.RakeRecordMapper;
import com.dk.provider.repay.api.XSReplaceApi;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.entity.RecordUserRate;
import com.dk.provider.repay.entity.RepayPlan;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.mapper.RepayPlanMapper;
import com.dk.provider.repay.service.IPaymentDetailService;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.mapper.UserMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class PaymentDetailServiceImpl extends BaseServiceImpl<PaymentDetail> implements IPaymentDetailService {
    private Logger logger = LoggerFactory.getLogger(PaymentDetailServiceImpl.class);

    @Resource
    private PaymentDetailMapper paymentDetailMapper;
    @Resource
    public void setSqlMapper (PaymentDetailMapper paymentDetailMapper)
    {
        super.setBaseMapper (paymentDetailMapper);
    }

    @Resource
    private XSReplaceApi xsReplaceApi;


    @Resource
    private RepayPlanMapper repayPlanMapper;

    @Resource
    private SubchannelMapper subchannelMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RouteInfoMapper routeInfoMapper;
    @Resource
    private RakeRecordMapper rakeRecordMapper;

    private DecimalFormat df = new DecimalFormat("#.00");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");


    @Override
    public List<PaymentDetail> query(Map map) throws Exception {
        return this.query(map);
    }

    @Override
    public int insert(PaymentDetail paymentDetail) throws Exception {
        return this.insert(paymentDetail);
    }

    @Override
    public int update(PaymentDetail paymentDetail) throws Exception {
        return this.update(paymentDetail);
    }

    @Override
    public void delete(Long id) throws Exception {

    }

    @Override
    public PaymentDetail queryByid(Long id) {
        return this.queryByid(id);
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

    /**
     * 定时任务执行还款计划
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
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
                    if (bean.getType() == 0) {
                        //TODO 调用快捷接口
                        logger.info("调用快捷接口");
                        /**
                         * 根据计划id查询路由小类通道id
                         */
                        RepayPlan repayPlan = repayPlanMapper.selectByPrimaryKey(bean.getPlanId());
                        if(repayPlan != null){//查询通道详细信息
                            Subchannel subchannel = subchannelMapper.queryByid(Long.valueOf(repayPlan.getSubId()));
                            if(subchannel != null){
                                if("xsdedh".equals(subchannel.getTabNo())){//新生代还收款
                                    xsReplaceApi.Xsreceiptapply(bean,repayPlan,subchannel);
                                }
                            }
                        }
                    }
                    if (bean.getType() == 1) {
                        //TODO 调用代还接口
                        logger.info("调用代还接口");
                        /**
                         * 根据计划id查询路由小类通道id
                         */
                        RepayPlan repayPlan = repayPlanMapper.selectByPrimaryKey(bean.getPlanId());
                        if(repayPlan != null){//查询通道详细信息
                            Subchannel subchannel = subchannelMapper.queryByid(Long.valueOf(repayPlan.getSubId()));
                            if(subchannel != null){
                                if("xsdedh".equals(subchannel.getTabNo())){//新生代还代付
                                    xsReplaceApi.Xspay(bean,repayPlan,subchannel);
                                }
                            }
                        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDetailState(JSONObject jsonObject) {
        String orderNo = jsonObject.getString("orderNo");//订单号
        String states = jsonObject.getString("states");//订单状态
        String respMsg = jsonObject.getString("respMsg");//订单描述
        String orderType = jsonObject.getString("orderType");//订单类型(1快捷，2代还)

        Map<String ,String> map = new HashMap<>();
        map.put("orderNo",orderNo);
        List<PaymentDetail> paymentDetailList = paymentDetailMapper.query(map);
        if(!paymentDetailList.isEmpty()){
            String amount = paymentDetailList.get(0).getAmount();
            String pmStates = paymentDetailList.get(0).getStatus().toString();
            logger.info("平台订单状态pmStates:{}",pmStates);
            if(!pmStates.equals("1")){//平台成功
                /**
                 * 根据计划id 修改该笔详情的订单号
                 */
                PaymentDetail paymentDetail1 = new PaymentDetail();
                paymentDetail1.setId(paymentDetailList.get(0).getId());
                paymentDetail1.setRemark(respMsg);
                paymentDetail1.setStatus(Long.valueOf(states));
                paymentDetailMapper.updateByPrimaryKeySelective(paymentDetail1);

                /**
                 * 订单成功 计算返佣 并新增 返佣记录
                 */
                if ("1".equals(states)) {
                    /**
                     * 查询计划汇总的通道
                     */
                    RepayPlan repayPlan = repayPlanMapper.selectByPrimaryKey(paymentDetail1.getId());
                    //查询当前人的通道费率和等级
                    String routeId = repayPlan.getRoutId();//大类通道id
                    String userId = repayPlan.getUserId().toString();//用户id


                    /**
                     * 查询用户所有上级
                     */
                    map.put("userId", userId);
                    List<String> list = userMapper.findSuperior(map);
                    if (list != null) {//
                        List<RecordUserRate> recordUserRateList = new LinkedList<>();//获取所有上级用户费率
                        for (int i = 0; i < list.size(); i++) {//循环遍历上级用户费率(逐级递增)
                            User user = userMapper.queryByid(Long.valueOf(list.get(i)));//查询用户等级
                            if (user != null) {
                                String classId = user.getClassId().toString();
                                map.put("classId", classId);//等级id
                                map.put("routeId", routeId);//大类通道id
                                List<RouteInfo> routeInfoList = routeInfoMapper.query(map);//根据用户等级查通道费率
                                String rate = routeInfoList.get(0).getRate();

                                RecordUserRate recordUserRate = new RecordUserRate();
                                recordUserRate.setUserId(userId);
                                recordUserRate.setClassId(classId);
                                recordUserRate.setRate(rate);
                                recordUserRate.setRouteId(routeId);
                                recordUserRateList.add(recordUserRate);
                            }
                        }
                        /**
                         * 级别费率差
                         */
                        if (recordUserRateList != null) {//遍历用户费率
                            for (int j = 0; j < recordUserRateList.size(); j++) {
                                if (j + 1 < recordUserRateList.size()) {
                                    Double ben = Double.valueOf(recordUserRateList.get(j).getRate());//本级
                                    Double sha = Double.valueOf(recordUserRateList.get(j + 1).getRate());//上级
                                    Double rateerr = ben - sha;//费率差
                                    if (rateerr > 0) {
                                        //返佣金额
                                        Double rakeamount = Double.valueOf(amount) * rateerr;
                                        String rakeamounts = df.format(rakeamount);
                                        //新增每笔返佣金额 记录
                                        RakeRecord rakeRecord = new RakeRecord();
                                        rakeRecord.setOrderNo(orderNo);//订单号
                                        rakeRecord.setOrderType(Long.valueOf(orderType));//订单类型
                                        rakeRecord.setOrderUserId(Long.valueOf(recordUserRateList.get(j).getUserId()));//得到返佣用户id
                                        rakeRecord.setUserId(Long.valueOf(userId));//订单用户id
                                        rakeRecord.setRokeAmt(rakeamounts);//返佣金额
                                        int rake = rakeRecordMapper.insert(rakeRecord);

                                        return rake;
                                    }
                                }


                            }
                        }

                    }
                }
            }

        }


        return 0;
    }


    /**
     * 根据时间对list元素排序
     * @param list
     */
    public static void ListSort(List<PaymentDetail> list) {
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
