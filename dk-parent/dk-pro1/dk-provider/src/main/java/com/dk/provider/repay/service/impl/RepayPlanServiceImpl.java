package com.dk.provider.repay.service.impl;

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
import com.dk.provider.repay.entity.AddPlanParm;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.entity.RepayPlan;
import com.dk.provider.repay.mapper.PaymentDetailMapper;
import com.dk.provider.repay.mapper.RepayPlanMapper;
import com.dk.provider.repay.service.RepayPlanService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service("repayPlanService")
public class RepayPlanServiceImpl extends BaseServiceImpl<RepayPlan> implements RepayPlanService {
    private Logger logger = LoggerFactory.getLogger(ReceiveRecordServiceImpl.class);


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
        repayPlan.setOrderNo(CommonUtils.getOrderNo());//订单号
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
            //查询代还汇总
            Map<String,Object> planMap = new HashMap<>();
            planMap.put("id",repayPlan.getId());
            RepayPlan repayPlanBean = repayPlanMapper.queryPlan(planMap);

            //查询代还明细
            Map<String,Object> planDetailMap = new HashMap<>();
            planDetailMap.put("plantId",repayPlan.getId());
            List<PaymentDetail> paymentDetailsBean = paymentDetailMapper.queryDetail(planDetailMap);

            Map resulMap = new HashMap();
            resulMap.put("repayPlan",repayPlanBean);
            resulMap.put("paymentDetails",paymentDetailsBean);

            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"新增成功",resulMap);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"新增失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResult activePlan(Map map) {
        RestResult restResult = new RestResult();
        int planNum = repayPlanMapper.activePlan(map);
        int detailNum = repayPlanMapper.activeDetail(map);
        if (planNum > 0 && detailNum > 0) {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"激活成功");
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"激活失败");
        }
    }
}
