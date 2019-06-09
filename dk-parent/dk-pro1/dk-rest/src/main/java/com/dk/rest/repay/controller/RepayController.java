package com.dk.rest.repay.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Pageable;
import com.common.controller.BaseController;
import com.common.utils.StringUtil;
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
import com.dk.rest.api.inter.replace.XSReplaceApi;
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
                    repayBindBean.setBillTime(e.getBillTime());
                    repayBindBean.setRepTime(e.getRepTime());
                    /**
                     * 查询该卡号最后一笔还款计划
                     */
                    Map<String ,Object> maprepay = new HashMap<>();
                    maprepay.put("cardCode",e.getCardCode());//绑定信用卡
                    maprepay.put("states","1");
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



    @ResponseBody
    @RequestMapping(value = {"/pageHistory"},method = RequestMethod.POST)
    public RestResult pageHistory(@RequestBody Map map){
        RestResult restResult = new RestResult();
        logger.info("进入RepayController的"+"pageHistory"+"方法,参数为:{}",map);
        try {
            if (StringUtil.isNotEmpty(map.get("userId"))) {
                Pageable pageable = new Pageable();
                if (StringUtil.isNotEmpty(map.get("pageNumber"))) {
                    pageable.setPageNumber(Integer.valueOf(map.get("pageNumber").toString()));
                }
                if (StringUtil.isNotEmpty(map.get("pageSize"))) {
                    pageable.setPageSize(Integer.valueOf(map.get("pageSize").toString()));
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
                if (!StringUtil.isNotEmpty(parm.getCity()) && !(parm.getProvince()).equals("香港") && !(parm.getProvince()).equals("澳门")) {
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
                    Map map = repayPlanService.searchPlanAndDetail(vo.getPlanId(),null,null,null);
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

    /**
     * 取消还款计划
     * @param map
     * @return
     */
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


    @ResponseBody
    @RequestMapping(value = {"/searchActivePlan"},method = RequestMethod.POST)
    public RestResult searchActivePlan(@RequestBody RepayPlan vo){
        try {
            RestResult restResult = new RestResult();
            logger.info("进入RepayController的"+"searchActivePlan"+"方法,参数为:{}",vo.getUserId(),vo.getCardCode(),1);
            if (StringUtil.isNotEmpty(vo)) {
                if (!StringUtil.isNotEmpty(vo.getUserId())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
                }
                if (!StringUtil.isNotEmpty(vo.getCardCode())) {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"卡号不能为空");
                }
                Map map = repayPlanService.searchPlanAndDetail(null,vo.getUserId(),vo.getCardCode(),1l);
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",map);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
            }
        } catch (Exception e){
            logger.error("searchActivePlan"+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }
}
