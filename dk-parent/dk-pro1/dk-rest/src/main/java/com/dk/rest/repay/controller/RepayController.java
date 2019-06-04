package com.dk.rest.repay.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Pageable;
import com.common.controller.BaseController;
import com.common.utils.CommonUtils;
import com.common.utils.StringUtil;
import com.dk.provider.plat.entity.RouteUser;
import com.dk.provider.plat.entity.SubUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.AddPlanParm;
import com.dk.provider.repay.entity.PaymentDetail;
import com.dk.provider.repay.entity.RepayPlan;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.provider.repay.service.RepayPlanService;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.service.ICardInfoService;
import com.dk.provider.user.service.IUserService;
import com.dk.rest.api.inter.XSReplaceApi;
import com.dk.rest.repay.bean.RepayBindBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repay")
public class RepayController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(RepayController.class);

    private DecimalFormat df = new DecimalFormat("#.00");//保留两位小数

    @Resource
    private SubchannelService subchannelService;

    @Resource
    private XSReplaceApi xsReplaceApi;

    @Resource
    private RouteInfoService routeInfoService;

    @Resource
    private IUserService userServiceImpl;

    @Resource
    private ICardInfoService cardInfoServiceImpl;

    @Resource
    private RepayPlanService repayPlanService;

    @Resource
    private ReceiveRecordService receiveRecordService;
    /**
     * 代还签约公共接口
     * 查询是否
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/signing"},method = RequestMethod.POST)
    public RestResult signing(@RequestBody JSONObject jsonObject){
        RestResult restResult = new RestResult();
        String method = "signing";
        logger.info("进入RepayController的"+method+"方法,参数为:{}",jsonObject);
        try {
            String routId = jsonObject.getString("routId");//大类通道id
            String subId = jsonObject.getString("subId");//小类通道id
            String userId = jsonObject.getString("userId");//用户id
            String cardCode = jsonObject.getString("cardCode");//签约卡号
            /**
             * 大类通道id
             * 根据卡号和用户id查询是否在该大类通道签约
             */
            Map<String,Object> map = new HashMap<>();
            map.put("userId",userId);
            map.put("cardNo",cardCode);//签约卡号
            map.put("subId",subId);
            SubUser subUser = subchannelService.querySubuser(map);
            if(subUser != null){//如果该卡已签约，返回签约标识
                JSONObject json = new JSONObject();
                json.put("isSign","1");//1已签约，0未签约
                return getRestResult(ResultEnume.SUCCESS,"该卡已签约,无需重复签约",json);
            }
            /**
             *根据小类通道id路由通道
             */
            Subchannel subchannel = subchannelService.queryByid(Long.valueOf(subId));
            if(subchannel != null){
                if("xsdedh".equals(subchannel.getTabNo())){//新生大额代还申请签约
                    restResult = xsReplaceApi.Xsapply(jsonObject,subchannel);
                }
            }else{
                restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"当前路由通道不可用");
            }

            return restResult;
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }


    /**
     * 签约确认
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/confirm"},method = RequestMethod.POST)
    public RestResult confirm(@RequestBody JSONObject jsonObject){
        RestResult restResult = new RestResult();
        String method = "confirm";
        logger.info("进入RepayController的"+method+"方法,参数为:{}",jsonObject);
        try {
            String subId = jsonObject.getString("subId");//小类通道id

            /**
             *根据小类通道id路由通道
             */
            Subchannel subchannel = subchannelService.queryByid(Long.valueOf(subId));
            if(subchannel != null){
                if("xsdedh".equals(subchannel.getTabNo())){//新生大额代还申请签约
                    restResult = xsReplaceApi.Xsconfirm(jsonObject,subchannel);
                }
            }else{
                restResult = restResult.setCodeAndMsg(ResultEnume.FAIL,"当前路由通道不可用");
            }

            return restResult;
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }


    /**
     * 查询已绑信用卡号查询代还金额统计(最近一笔记录即可)
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/bindrepay"},method = RequestMethod.POST)
    public RestResult bindRepay(@RequestBody JSONObject jsonObject){
        String method = "bindRepay";
        logger.info("进入RepayController的"+method+"方法,参数为:{}",jsonObject);
        try {
            String userId = jsonObject.getString("userId");//用户id
            /**
             * 查询已绑定信用卡
             */
            Map<String,Object> mapcard = new HashMap<>();
            mapcard.put("userId",userId);
            mapcard.put("type","02");
            List<CardInfo> cardInfoList = cardInfoServiceImpl.search(mapcard);
            if(cardInfoList == null){
                return getRestResult(ResultEnume.FAIL,"没有找到绑定信用卡",new JSONObject());
            }
            List<RepayBindBean> repayBindBeanList = new LinkedList<>();
            if(cardInfoList.size() > 0){
                for(CardInfo e : cardInfoList){
                    RepayBindBean repayBindBean = new RepayBindBean();
                    repayBindBean.setCardCode(e.getCardCode());
                    repayBindBean.setBankName(e.getBankName());
                    repayBindBean.setBankCode(e.getBankCode());
                    /**
                     * 查询该卡号最后一笔还款计划
                     */
                    Map<String ,Object> maprepay = new HashMap<>();
                    maprepay.put("cardCode",e.getCardCode());//绑定信用卡
                    List<RepayPlan> repayPlanLit = repayPlanService.query(maprepay);
                    if(repayPlanLit.size() > 0){
                        String totalamt =  repayPlanLit.get(0).getAmount();
                        if(StringUtils.isEmpty(totalamt)){//totalamt = totalamt==null ? "0" : totalamt;
                            totalamt = "0";
                        }
                        String returnAmt = repayPlanLit.get(0).getReturnAmt();
                        if(StringUtils.isEmpty(returnAmt)){// returnAmt = returnAmt=="" ? "0" : returnAmt;
                            returnAmt = "0";
                        }

                        repayBindBean.setTotalAmt(totalamt);
                        repayBindBean.setReturnAmt(returnAmt);
                        Double notyet = Double.valueOf(totalamt) - Double.valueOf(returnAmt);
                        repayBindBean.setNotyetAmt(df.format(notyet));
                    }
                    repayBindBeanList.add(repayBindBean);
                }
            }
            return getRestResult(ResultEnume.SUCCESS,"查询成功",repayBindBeanList);
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 新增还款计划 （还款汇总）
     * @param jsonObject
     * @return
     *//*
    @ResponseBody
    @RequestMapping(value = {"/addplan"},method = RequestMethod.POST)
    public RestResult addplan(@RequestBody JSONObject jsonObject){
        String method = "addplan";
        logger.info("进入RepayController的"+method+"方法,参数为:{}",jsonObject);
        try {
            *//**
             *参数
             *//*
            int plandays = 0;//计划实际天数
            String realbeginTime = "";//实际开始日期
            String realendTime = "";//实际结束日期

            String amount = jsonObject.getString("amount");//还款金额
            String reseAmt = jsonObject.getString("reseAmt");//预留金额
            String planBegin = jsonObject.getString("planBegin");//计划开始日期
            String planEnd = jsonObject.getString("planEnd");//计划结束日期
            String datearr = jsonObject.getString("datearr");//实际执行日期字符串
            if(!StringUtils.isEmpty(datearr)){// 05/29,05/30,
                datearr = datearr.replace("/","-");
                String[] dateStrarr = datearr.split(",");//注意分隔符是需要转译,得到日期字符串数组
                plandays = dateStrarr.length;//计划的实际天数

                //实际开始日期 和 实际结束日期
                realbeginTime = dateStrarr[0];//实际开始日期
                realendTime = dateStrarr[dateStrarr.length-1];//实际结束日期
            }

            String planArea = jsonObject.getString("planArea"); //计划消费地区,省份城市
            String cardCode = jsonObject.getString("cardCode");//还款信用卡 卡号
            String userId = jsonObject.getString("userId");//用户id
            String routId = jsonObject.getString("routId");//大类通道id
            String subId = jsonObject.getString("subId");//小类通道id

            *//**
             * 费率手续费 还款金额 * 费率  + 单笔 * 笔数
             * 计算出笔数  天数 日期来算
             * 计算笔数（还款金额/预留金额 ， 如果商等于整数，则笔数=商+1.如果商不等于整数有余数，则笔数=商+1+1）
             * 根据还款金额和卡内预留金额拆分笔数
             *//*
            int yunum = Integer.valueOf(amount) % Integer.valueOf(reseAmt);
            int pennum = Integer.valueOf(amount) / Integer.valueOf(reseAmt);//还款笔数
            if(yunum != 0){
                pennum = pennum+2;
            }else{
                pennum = pennum+1;
            }
            *//**
             * 笔数和天数的关系  一天最多三笔,
             * 笔数/3笔 + 1= 最多天数 ，最多天数 <= 计划天数
             *//*
            //plandays = CommonUtils.daysOfTwo(planBegin,planEnd);//计划实际天数
            int actdays = pennum/3 + 1;//执行实际天数
            if( actdays > plandays){
                String msg = "当前卡内预留金额,还款"+amount+"元至少需要"+actdays+"天,请增加还款天数或提高卡内预留金额";
                return getRestResult(ResultEnume.FAIL,msg,new JSONObject());
            }

            *//**
             * 计算还款需要的手续费
             * 根据用户id查询选择的大类通道费率
             *//*
            Map<String ,Object> maps = new HashMap<>();
            maps.put("userId",userId);
            maps.put("routId",routId);
            RouteUser routeUser = routeInfoService.queryUserRout(maps);
            if(routeUser == null){
                return getRestResult(ResultEnume.FAIL,"未找到还款通道",new JSONObject());
            }
            //还款需要的总手续费 = 还款金额 * 费率 + 笔数 * 单笔
            Double totalfee = Double.valueOf(amount) * Double.valueOf(routeUser.getRate()) + pennum * Double.valueOf(routeUser.getFee());
            totalfee = Double.valueOf(df.format(totalfee));
            //执行计划需预留的余额为 预留金额+ 总手续费
            Double totreseAmt = Double.valueOf(reseAmt) + totalfee;
            totreseAmt = Double.valueOf(df.format(totreseAmt));


            JSONObject jsonRes = new JSONObject();
            jsonRes.put("realbeginTime",realbeginTime);
            jsonRes.put("realendTime",realendTime);
            jsonRes.put("amount",amount);
            jsonRes.put("pennum",pennum);
            jsonRes.put("totalfee",totalfee);
            jsonRes.put("totreseAmt",totreseAmt);
            jsonRes.put("cardCode",cardCode);


            *//**
             * 计划详情
             * 还款笔数 、还款天数
             * 一扣一还
             * 二扣一还
             * 三扣一还
             * 一天最多扣三笔
             *//*
            //先随机分配每天还几笔并且小于等于3
            List list = CommonUtils.randomplan(pennum,plandays);
            //分别根据每天的还款笔数，制定消费和还款
            *//**
             * 首先确定还款时间
             *//*









            return getRestResult(ResultEnume.SUCCESS,"计划汇总",jsonRes);
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }*/



    @ResponseBody
    @RequestMapping(value = {"/pageHistory"},method = RequestMethod.POST)
    public RestResult pageHistory(@RequestBody Map map){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"pageHistory"+"方法,参数为:{}",map);
        try {
            if (StringUtil.isNotEmpty(map.get("userId"))) {
                Pageable pageable = new Pageable();
                if (StringUtil.isNotEmpty(map.get("pageNumber"))) {
                    pageable.setPageNumber((int)map.get("pageNumber"));
                }
                if (StringUtil.isNotEmpty(map.get("pageSize"))) {
                    pageable.setPageSize((int)map.get("pageSize"));
                }
                return receiveRecordService.pageHistory(map,pageable);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
            }
        }catch (Exception e){
            logger.error("pageHistory"+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }


    @ResponseBody
    @RequestMapping(value = {"/addPlan"},method = RequestMethod.POST)
    public RestResult addPlan(@RequestBody AddPlanParm parm){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"addPlan"+"方法,参数为:{}",JSONObject.toJSONString(parm));
        try {
                if (!StringUtil.isNotEmpty(parm.getSetAmount())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"预留金额不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getStartDate())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"还款开始时间不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getBillDate())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"还款结束时间不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getBillAmount())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"还款总额不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getDateListStr())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"执行日期的字符串不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getUserId())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getCardCode())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"卡号不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getProvince())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"省份不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getCity())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"地市不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getRoutId())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"大类通道id不能为空");
                }
                if (!StringUtil.isNotEmpty(parm.getSubId())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"肖类通道id不能为空");
                }
                return repayPlanService.addPlan(parm);
        }catch (Exception e){
            logger.error("addPlan"+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    @ResponseBody
    @RequestMapping(value = {"/activePlan"},method = RequestMethod.POST)
    public RestResult activePlan(@RequestBody Map map){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"activePlan"+"方法,参数为:{}",map);
        if (StringUtil.isNotEmpty(map)) {
            if (StringUtil.isNotEmpty(map.get("planId"))) {
                try {
                    return repayPlanService.activePlan(map);
                }catch (Exception e){
                    logger.error("activePlan"+"执行出错:{}",e.getMessage());
                    e.printStackTrace();
                    return getFailRes();
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"计划ID不能为空");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }

    }



    @ResponseBody
    @RequestMapping(value = {"/queryPlanByUser"},method = RequestMethod.POST)
    public RestResult queryPlanByUser(@RequestBody Map map){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"queryPlanByUser"+"方法,参数为:{}",map);
        if (StringUtil.isNotEmpty(map)) {
            if (StringUtil.isNotEmpty(map.get("userId"))) {
                try {
                    return repayPlanService.queryPlanByUser(map);
                }catch (Exception e){
                    logger.error("queryPlanByUser"+"执行出错:{}",e.getMessage());
                    e.printStackTrace();
                    return getFailRes();
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户ID不能为空");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }

    }



    @ResponseBody
    @RequestMapping(value = {"/searchPlanAndDetail"},method = RequestMethod.POST)
    public RestResult searchPlanAndDetail(@RequestBody PaymentDetail vo){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"searchPlanAndDetail"+"方法,参数为:{}",vo.getPlanId());
        if (StringUtil.isNotEmpty(vo)) {
            if (StringUtil.isNotEmpty(vo.getPlanId())) {
                try {
                    Map map = repayPlanService.searchPlanAndDetail(vo.getPlanId());
                    return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",map);
                }catch (Exception e){
                    logger.error("searchPlanAndDetail"+"执行出错:{}",e.getMessage());
                    e.printStackTrace();
                    return getFailRes();
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"计划ID不能为空");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }

    }

    @ResponseBody
    @RequestMapping(value = {"/cancalPlan"},method = RequestMethod.POST)
    public RestResult cancalplan(@RequestBody Map map){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"activePlan"+"方法,参数为:{}",map);
        if (StringUtil.isNotEmpty(map)) {
            if (StringUtil.isNotEmpty(map.get("planId"))) {
                try {
                    return repayPlanService.cancelPlan(map);
                }catch (Exception e){
                    logger.error("activePlan"+"执行出错:{}",e.getMessage());
                    e.printStackTrace();
                    return getFailRes();
                }
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"计划ID不能为空");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }

    }

}
