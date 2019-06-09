package com.dk.provider.repay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.Bill.BillPaymentHandler;
import com.common.Bill.BillPaymentParam;
import com.common.Bill.BillPaymentPlan;
import com.common.Bill.ChargeDetail;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.utils.CommonUtils;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.repay.entity.*;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.mapper.RepayPlanMapper;
import com.dk.provider.repay.service.RepayPlanService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.math.BigDecimal;
import java.util.*;

@Service("repayPlanService")
public class RepayPlanServiceImpl extends BaseServiceImpl<RepayPlan> implements RepayPlanService {
    private Logger logger = LoggerFactory.getLogger(ReceiveRecordServiceImpl.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Resource
    private RepayPlanMapper repayPlanMapper;
    @Resource
    private PaymentDetailMapper paymentDetailMapper;
    @Resource
    private RouteInfoService routeInfoService;
    @Resource
    public void setSqlMapper (RepayPlanMapper repayPlanMapper)
    {
        super.setBaseMapper (repayPlanMapper);
    }


    @Override
    public int updreturnAmt(Map map) {
        return repayPlanMapper.updreturnAmt(map);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult addPlan(AddPlanParm parm) {
        RestResult restResult = new RestResult();

        //还款计划
        RepayPlan repayPlan = new RepayPlan();
        //还款明细
        List<PaymentDetail> paymentDetailList = new ArrayList<>();

        //还款拆分算法
        BillPaymentHandler billPaymentHandler = new BillPaymentHandler();

        /*BigDecimal setAmount = new BigDecimal(2500.22).setScale(2, BigDecimal.ROUND_CEILING);
        String startDate = "2019-06-01";
        String billDate = "2019-06-05";
        BigDecimal billAmount = new BigDecimal(10000);
        BigDecimal chargeRate = new BigDecimal(0.0065);
        BigDecimal paymentRate = BigDecimal.ONE;
        String dateListStr = "2019-06-01,2019-06-03,2019-06-04,2019-06-05,";*/


        //查询用户费率和单笔手续费
        Map<String,Object> userRateParm = new HashMap<>();
        userRateParm.put("userId",parm.getUserId());
        userRateParm.put("routId",parm.getRoutId());
        List<RouteInfo> routeInfos = routeInfoService.findUserrate(userRateParm);
        RouteInfo routeInfo = new RouteInfo();
        if (routeInfos != null && routeInfos.size() > 0) {
            routeInfo = routeInfos.get(0);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"系统内暂无您的费率信息");
        }


        BigDecimal setAmount = new BigDecimal(parm.getSetAmount()).setScale(2, BigDecimal.ROUND_CEILING);
        String startDate = parm.getStartDate();
        String billDate = parm.getBillDate();
        BigDecimal billAmount = new BigDecimal(parm.getBillAmount());
        BigDecimal chargeRate = new BigDecimal(Double.parseDouble(routeInfo.getRate()));
        BigDecimal paymentRate = new BigDecimal(Double.parseDouble(routeInfo.getFee()));
        String dateListStr = parm.getDateListStr();


        BillPaymentParam billPaymentParam = new BillPaymentParam(dateListStr, setAmount, billAmount, startDate, billDate, chargeRate, paymentRate);
        BillPaymentPlan billPaymentPlan = billPaymentHandler.getBillPaymentPlan(billPaymentParam);

        repayPlan.setUserId(parm.getUserId());//用户id
        repayPlan.setCardCode(parm.getCardCode());//卡号
        repayPlan.setBillTime(billDate);//账单日
        repayPlan.setRepTime(billPaymentPlan.getPaymentDates().get(0));//实际还款开始日期
        repayPlan.setPlanBegin(startDate);//计划开始日期
        repayPlan.setPlanEnd(billDate);//计划结束日期
        repayPlan.setAmount(String.valueOf(billAmount));//还款金额
        repayPlan.setReseAmt(String.valueOf(setAmount));//卡内预留金额
        repayPlan.setReturnAmt("0");//已还金额
        repayPlan.setPlanArea(parm.getProvince());//省份
        repayPlan.setPlanCity(parm.getCity());//地市
        repayPlan.setStates(-1l);//状态
        repayPlan.setCreateTime(new Date());//创建时间
        repayPlan.setRoutId(parm.getRoutId());//大类通道
        repayPlan.setSubId(parm.getSubId());//小类通道
        repayPlan.setNotyetAmt(String.valueOf(billAmount));//为还金额
        repayPlan.setOrderNo(CommonUtils.getOrderNo(2l));//订单号
        repayPlan.setReturnTimes(String.valueOf(billPaymentPlan.getPaymentTimes()));//还款次数
        repayPlan.setReeTime(billPaymentPlan.getPaymentDates().get(billPaymentPlan.getPaymentDates().size()-1));//实际还款结束日期
        //手续费
        repayPlan.setHandlingFee(String.valueOf((billPaymentPlan.getChargeAmount().subtract(billPaymentPlan.getBillAmount())).add(billPaymentPlan.getPaymentAmount().subtract(billPaymentPlan.getBillAmount()))));
        //新增还款计划汇总
        int repayPlanNum = repayPlanMapper.insert(repayPlan);
        if (repayPlanNum > 0) {
            //新增还款明细（代还）
            for (com.common.Bill.PaymentDetail bean : billPaymentPlan.getPaymentDetailList()) {
                PaymentDetail paymentDetail = new PaymentDetail();
                paymentDetail.setPlanId(repayPlan.getId());//计划id
                paymentDetail.setAmount(String.valueOf(bean.getPaymentAmount()));//消费金额
                paymentDetail.setActiveTime(bean.getPaymentDate());//消费时间
                paymentDetail.setCreateTime(new Date());//创建时间
                paymentDetail.setType(1l);//类型
                paymentDetail.setStatus(-1l);//状态
                paymentDetail.setUserId(parm.getUserId());//用户ID
                paymentDetail.setCardNo(parm.getCardCode());//卡号
                paymentDetailList.add(paymentDetail);
            }

            //新增还款明细（代扣）
            for (ChargeDetail bean :billPaymentPlan.getChargeDetails()) {
                PaymentDetail paymentDetail = new PaymentDetail();
                paymentDetail.setPlanId(repayPlan.getId());//计划id
                paymentDetail.setAmount(String.valueOf(bean.getChargeAmount()));//消费金额
                paymentDetail.setActiveTime(bean.getChargeTime());//消费时间
                paymentDetail.setCreateTime(new Date());//创建时间
                paymentDetail.setType(0l);//类型
                paymentDetail.setStatus(-1l);//状态
                paymentDetail.setUserId(parm.getUserId());//用户ID
                paymentDetail.setCardNo(parm.getCardCode());//卡号
                paymentDetailList.add(paymentDetail);
            }
            //根据时间排序
            PaymentDetailServiceImpl.ListSort(paymentDetailList);
            //新增明细
            int insertDetailNum = paymentDetailMapper.insertList(paymentDetailList);
            if (insertDetailNum > 0) {
                logger.info("新增明细成功");
            }

            //查询计划汇总以及详情
            Map resultMap = this.searchPlanAndDetail(repayPlan.getId(),null,null,null);

            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"新增成功",resultMap);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"新增失败");
        }
    }


    /**
     * 查询计划汇总以及详情
     * @param plantId 计划id
     * @param userId 用户id
     * @param cardNo 用户卡号
     * @param status 状态
     * @return
     */
    @Override
    public Map searchPlanAndDetail (Long plantId,Long userId,String cardNo,Long status) {
        List<PaymentDetail> paymentDetailsBean = new ArrayList<>();
        List<PaymentDetailVO> paymentDetailsVO = new ArrayList<>();
        //查询代还汇总
        Map<String,Object> parmMap = new HashMap<>();
        parmMap.put("plantId",plantId);
        parmMap.put("userId",userId);
        parmMap.put("cardNo",cardNo);
        parmMap.put("status",status);
        RepayPlan repayPlanBean = repayPlanMapper.queryPlan(parmMap);
        RepayPlanVO repayPlanVO = new RepayPlanVO();
        if (repayPlanBean != null) {

            if (repayPlanBean.getStates() == -1) {
                repayPlanBean.setStatesStr("预览");
            } else if (repayPlanBean.getStates() == 0) {
                repayPlanBean.setStatesStr("未执行");
            } else if (repayPlanBean.getStates() == 1) {
                repayPlanBean.setStatesStr("执行中");
            } else if (repayPlanBean.getStates() == 2) {
                repayPlanBean.setStatesStr("完成");
            } else if (repayPlanBean.getStates() == 3) {
                repayPlanBean.setStatesStr("已取消");
            }

            CommonUtils.reflect(repayPlanBean);
            BeanUtils.copyProperties(repayPlanBean,repayPlanVO);

            //查询代还明细
            parmMap.put("plantId",repayPlanBean.getId());
            paymentDetailsBean = paymentDetailMapper.queryDetail(parmMap);
            if (paymentDetailsBean!= null && !paymentDetailsBean.isEmpty()) {
                for (PaymentDetail bean : paymentDetailsBean) {
                    PaymentDetailVO paymentDetailVO = new PaymentDetailVO();
                    if (bean.getStatus() == -1) {
                        bean.setStatusStr("预览");
                    } else if (bean.getStatus() == 0) {
                        bean.setStatusStr("未执行");
                    } else if (bean.getStatus() == 1) {
                        bean.setStatusStr("执行中");
                    } else if (bean.getStatus() == 2) {
                        bean.setStatusStr("完成");
                    } else if (bean.getStatus() == 3) {
                        bean.setStatusStr("已取消");
                    } else if (bean.getStatus() == 4) {
                        bean.setStatusStr("失败");
                    }


                    if (bean.getType() == 0) {
                        bean.setTypeStr("消费");
                    } else if (bean.getType() == 1) {
                        bean.setTypeStr("还款");
                    }
                    CommonUtils.reflect(bean);
                    BeanUtils.copyProperties(bean,paymentDetailVO);
                    paymentDetailsVO.add(paymentDetailVO);
                }
            }
        }



        Map resultMap = new HashMap();
        resultMap.put("repayPlan",repayPlanVO!=null?repayPlanVO:new JSONObject());
        resultMap.put("paymentDetails",(paymentDetailsVO!=null && paymentDetailsVO.size()>0)?paymentDetailsVO:new ArrayList<>());
        return resultMap;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult activePlan(Map map) {
        RestResult restResult = new RestResult();
        map.put("status","1");
        int planNum = repayPlanMapper.activePlan(map);
        int detailNum = repayPlanMapper.activeDetail(map);
        if (planNum > 0 && detailNum > 0) {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"激活成功");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"激活失败");
        }
    }

    @Override
    public RestResult queryPlanByUser(Map map) {
        RestResult restResult = new RestResult();
        try{
            List<RepayPlan> repayPlans = repayPlanMapper.queryPlanByUser(map);
            List<RepayPlanVO> repayPlansVO = new ArrayList<>();
            if (repayPlans != null && repayPlans.size() >0) {
                for (RepayPlan bean : repayPlans) {
                    RepayPlanVO repayPlanVOBean = new RepayPlanVO();
                    bean.setCreateTime(DateUtils.parseDate(sdf.format(bean.getCreateTime()),DATE_FORMAT));
                    CommonUtils.reflect(bean);
                    BeanUtils.copyProperties(bean,repayPlanVOBean);
                    repayPlansVO.add(repayPlanVOBean);
                }
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",repayPlansVO);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"暂无记录",repayPlansVO);
            }
        } catch (Exception e) {
            logger.info("错误信息："+e.getMessage());
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"查询失败");
        }
    }

    /**
     * 取消还款计划
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult cancelPlan(Map map) {
        RestResult restResult = new RestResult();
        map.put("status","3");
        int planNum = repayPlanMapper.activePlan(map);
        map.put("state","1");
        int detailNum = repayPlanMapper.activeDetail(map);
        if (planNum > 0 && detailNum > 0) {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"取消成功");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"取消失败");
        }
    }
}
