package com.dk.rest.plat.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.controller.BaseController;
import com.common.utils.CommonUtils;
import com.common.utils.StringUtil;
import com.dk.provider.basic.entity.Area;
import com.dk.provider.basic.entity.ProvinceCity;
import com.dk.provider.basic.service.AreaService;
import com.dk.provider.basis.service.RedisCacheService;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.RouteUser;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.rest.api.inter.BeiduoQuickPayApi;
import com.dk.rest.api.inter.XSquickPayApi;
import com.dk.rest.plat.bean.AreaBean;
import com.dk.rest.plat.bean.RouteInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * 通道信息
 */
@RestController
@RequestMapping("/route")
public class RouteController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(RouteController.class);


    @Resource
    private RouteInfoService routeInfoService;

    @Resource
    private AreaService areaService;
    /**
     * 收款流水记录
     */
    @Resource
    private ReceiveRecordService receiveRecordService;

    /**
     * 小类通道
     */
    @Resource
    private SubchannelService subchannelService;

    /**
     * 快捷K3
     */
    @Resource
    private BeiduoQuickPayApi beiduoQuickPayApi;
    /**
     * 快捷K1
     */
    @Resource
    private XSquickPayApi xSquickPayApi;
    /**
     * 查询大类通道信息（快捷，代还）
     * @param map
     * @param pageable
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/query"},method = RequestMethod.POST)
    public RestResult queryroute(@RequestBody Map map, Pageable pageable){
        String method = "queryroute";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",map);
        try{
            pageable.setPageSize(Integer.valueOf(map.get("pageSize").toString()));
            pageable.setPageNumber(Integer.valueOf(map.get("pageNumber").toString()));
            Page<RouteInfo> routeInfoPage = routeInfoService.page(map,pageable);
            Page<RouteInfoBean> page = new Page<>(new LinkedList<>(), routeInfoPage.getTotal(), pageable);
            List<RouteInfoBean> routeInfoBeanList = new LinkedList<>();
            if(routeInfoBeanList != null){
                for(RouteInfo routeInfo : routeInfoPage.getContent() ){
                    RouteInfoBean routeInfoBean = new RouteInfoBean();
                    BeanUtils.copyProperties(routeInfo,routeInfoBean);
                    /**
                     * 查询小类通道
                     */
                    Map<String,Object> mapRout = new HashMap<>();
                    mapRout.put("routId",routeInfo.getId());//大类通道id
                    List<Subchannel> subchannelList = subchannelService.selectByrout(mapRout);
                    if(subchannelList != null ) {
                        /**
                         * 随机取一条
                         */
                        Random random = new Random();
                        int n = random.nextInt(subchannelList.size());//随机数
                        Subchannel subchannel = subchannelList.get(n);//取出的随机一条数据
                        Long subId = subchannel.getId();//小类通道id
                        routeInfoBean.setSubId(subId.toString());
                    }
                    CommonUtils.reflect(routeInfoBean);
                    routeInfoBeanList.add(routeInfoBean);
                }
                page.getContent().addAll(routeInfoBeanList);

                return getRestResult(ResultEnume.SUCCESS,ResultEnume.SUCSTR,page);
            }

            return null;
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 生成订单(快捷)
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/order"},method = RequestMethod.POST)
    public RestResult order(@RequestBody JSONObject jsonObject){
        String method = "order";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",jsonObject);
        try{
            /**
             * 订单金额    amount
             * 交易通道id  routId
             * 用户id     userId
             * 费率       rate --
             * 单笔       fee  --
             * 交易卡号    receCard
             * 到账卡号    settleCard  --
             * 可选(省份，城市，行业，商户)后面拓展
             * 省份 province
             * 城市 city
             * 行业 industry
             * 商户 merchant
             * resultType 返回类型（1短信验证，2返回url地址,）
             */

            /**
             * 随机路由小类通道
             */
            Map<String,Object> map = new HashMap<>();
            map.put("routId",jsonObject.get("routId"));//大类通道id
            map.put("subId",jsonObject.get("subId"));//小类通道id
            List<Subchannel> subchannelList = subchannelService.selectByrout(map);
            if(subchannelList.size() >0){
                /**
                 * 随机取一条
                 */
                /*Random random = new Random();
                int n = random.nextInt(subchannelList.size());//随机数
                Subchannel subchannel = subchannelList.get(n);//取出的随机一条数据*/
                String tabNo = subchannelList.get(0).getTabNo();//小类通道标记
                Long subId = subchannelList.get(0).getId();//小类通道id

                jsonObject.put("tabNo",tabNo);
                jsonObject.put("subId",subId);

                /**
                 * 根据用户id查询用户 到账卡号，费率等等
                 */
                String orderNo = CommonUtils.getOrderNo();
                Map<String ,Object> maps = new HashMap<>();
                maps.put("userId",jsonObject.getString("userId"));
                maps.put("routId",jsonObject.getString("routId"));
                RouteUser routeUser = routeInfoService.queryUserRout(maps);

                ReceiveRecord receiveRecord = new ReceiveRecord();
                receiveRecord.setOrderNo(orderNo);//订单号
                receiveRecord.setAmount(jsonObject.getString("amount"));//交易金额
                receiveRecord.setRate(routeUser.getRate());//费率
                receiveRecord.setFee(routeUser.getFee());//单笔手续费
                receiveRecord.setUserId(jsonObject.getString("userId"));//用户id
                receiveRecord.setReceCard(jsonObject.getString("receCard"));//交易卡
                receiveRecord.setRouteId(jsonObject.getString("routId"));//大类通道id
                receiveRecord.setSubId(subId.toString());//小类通道id
                receiveRecord.setSettleCard(routeUser.getSettleCard());//到账储蓄卡
                receiveRecord.setStates(2L);//状态1成功,2失败,0处理中
                receiveRecord.setProvince(jsonObject.getString("province"));//省份
                receiveRecord.setCity(jsonObject.getString("city"));//城市
                receiveRecord.setIndustry(jsonObject.getString("industry"));//行业
                receiveRecord.setMerchant(jsonObject.getString("merchant"));//商户
                receiveRecordService.insert(receiveRecord);

                jsonObject.put("orderNo",orderNo);//订单号
                jsonObject.put("rate",routeUser.getRate());
                jsonObject.put("fee",routeUser.getFee());
                /**
                 * 根据小类通道id
                 */
                return routeSubChan(jsonObject);
            }else{
                return getRestResult(ResultEnume.FAIL,"暂无可用路由通道",new JSONObject());
            }
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 路由小类通道
     * @param jsonObject
     */
    public RestResult routeSubChan(JSONObject jsonObject) throws Exception{
        String tabNo = jsonObject.getString("tabNo");
        if(tabNo.equals("beiduo")){//快捷K3
            return beiduoQuickPayApi.BDquickProcess(jsonObject);
        }else if(tabNo.equals("xskjk1")) {//新生快捷K1
            return xSquickPayApi.XsQuickapply(jsonObject);
        }else{
            return getRestResult(ResultEnume.FAIL,"暂无可用路由通道",new JSONObject());
        }
    }


    /**
     * 确认支付(快捷)
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/pay"},method = RequestMethod.POST)
    public RestResult pay(@RequestBody JSONObject jsonObject){
        String method = "pay";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",jsonObject);
        try{
            /**
             * smsCode 短信验证码
             * orderNo 订单号
             */
            if(!jsonObject.containsKey("smsCode") || jsonObject.get("smsCode") == null){
                return getRestResult(ResultEnume.FAIL,"短信验证码不能为空",new JSONObject());
            }
            if(!jsonObject.containsKey("orderNo") || jsonObject.get("orderNo") == null){
                return getRestResult(ResultEnume.FAIL,"订单号不能为空",new JSONObject());
            }
            String orderNo = jsonObject.getString("orderNo");

            Map<String,Object> map = new HashMap<>();
            map.put("orderNo",orderNo);
            List<ReceiveRecord> receiveRecordList = receiveRecordService.query(map);
            if(receiveRecordList ==null){
                return getRestResult(ResultEnume.FAIL,"未找到订单号",new JSONObject());
            }
            jsonObject.put("receCard",receiveRecordList.get(0).getReceCard());

            Map<String,Object> map1=new HashMap<>();
            map1.put("subId",receiveRecordList.get(0).getSubId());
            List<Subchannel> subchannelList = subchannelService.query(map1);
            if(subchannelList == null){
                return getRestResult(ResultEnume.FAIL,"未找到路由通道",new JSONObject());
            }

            String tabNo = subchannelList.get(0).getTabNo();
            if(tabNo.equals("xskjk1")) {//新生快捷K1
                return xSquickPayApi.XsQuickconfirm(jsonObject);
            }else if(tabNo.equals("beiduo")){//快捷K3
                return beiduoQuickPayApi.BDquickProcess(jsonObject);
            }

            return getRestResult(ResultEnume.FAIL,"订单确认失败",new JSONObject());
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 查询省
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/province"})
    public RestResult queryProvince(@RequestBody JSONObject jsonObject){
        String method = "queryProvince";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",jsonObject);
        try{
            /**
             * 查询省
             */
            Map<String,Object> map = new HashMap<>();
            map.put("level","1");
            List<Area> areaList = areaService.queryList(map);
            List<AreaBean> areaBeanList = new LinkedList<>();
            for(Area e : areaList){
                AreaBean areaBean = new AreaBean();
                BeanUtils.copyProperties(e,areaBean);
                CommonUtils.reflect(areaBean);
                areaBeanList.add(areaBean);
            }

            return getRestResult(ResultEnume.SUCCESS,"查询省成功",areaBeanList);
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 查询市
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/city"})
    public RestResult queryCity(@RequestBody JSONObject jsonObject){
        String method = "queryCity";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",jsonObject);
        if(StringUtils.isEmpty(jsonObject.get("parentId"))){
            return getRestResult(ResultEnume.FAIL,"省份id不能为空",new JSONObject());
        }
        try{
            /**
             * 根据上级省份查询市
             */
            Map<String,Object> map = new HashMap<>();
            map.put("parentId",jsonObject.get("parentId"));
            List<Area> areaList = areaService.queryList(map);
            List<AreaBean> areaBeanList = new LinkedList<>();
            for(Area e : areaList){
                AreaBean areaBean = new AreaBean();
                BeanUtils.copyProperties(e,areaBean);
                CommonUtils.reflect(areaBean);
                areaBeanList.add(areaBean);
            }
            return getRestResult(ResultEnume.SUCCESS,"查询市成功",areaBeanList);
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }

    /**
     * 查询行业
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/industry"},method = RequestMethod.POST)
    public RestResult queryIndusMer(JSONObject jsonObject){
        String method = "industry";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",jsonObject);
        try {

            return null;
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }
    /**
     * 查询商户
     * @param jsonObject
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/merchant"},method = RequestMethod.POST)
    public RestResult merchant(JSONObject jsonObject){
        String method = "merchant";
        logger.info("进入RouteController的"+method+"方法,参数为:{}",jsonObject);
        try {

            return null;
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }


    /**
     * 查询商户
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping(value = {"/parentInfo"},method = RequestMethod.POST)
    public RestResult parentInfo(@RequestBody Map map){
        logger.info("进入RouteController的parentInfo方法,参数为:{}",map);
        RestResult restResult = new RestResult();
        if (StringUtil.isNotEmpty(map)) {
            try {
                if (StringUtil.isNotEmpty(map.get("userId"))) {
                    return routeInfoService.parentRouteInfo(map);
                } else {
                    return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户id不能为空");
                }
            }catch (Exception e){
                logger.error("parentInfo执行出错:{}",e.getMessage());
                e.printStackTrace();
                return getFailRes();
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"参数错误");
        }
    }


    /**
     * 加载省份，地市
     * @return
     */
    @RequestMapping("/searchProvinceCity")
    public RestResult searchProvinceCity () {
        logger.info("进入RouteController的searchProvinceCity方法");
        RestResult restResult = new RestResult();
        Map<String,String> parm = new HashMap<>();
        parm.put("procity","true");
        try {
            List<Area> list = areaService.queryList(parm);
            if (StringUtil.isNotEmpty(list)) {
                List<ProvinceCity> provinceCities = new ArrayList<>();
                for (Area bean:list) {
                    if ((bean.getAreaLevel()).equals("1")) {
                        ProvinceCity provinceCity = new ProvinceCity();
                        provinceCity.setId(bean.getId());
                        provinceCity.setAreaName(bean.getAreaName());
                        provinceCity.setAreaFull(bean.getAreaFull());
                        provinceCity.setParentId(bean.getParentId());
                        provinceCity.setAreaLevel(bean.getAreaLevel());
                        provinceCities.add(provinceCity);
                    }
                }
                for (ProvinceCity bean:provinceCities) {
                    List<Area> cityList = new ArrayList<>();
                    for (Area areaList:list) {
                        if ((bean.getId()).equals(areaList.getParentId())) {
                            cityList.add(areaList);
                        }
                    }
                    bean.setCity(cityList);
                }
                restResult.setCodeAndMsg(ResultEnume.SUCCESS," 地市查询成功",provinceCities);
            } else {
                restResult.setCodeAndMsg(ResultEnume.FAIL," 地市查询失败");
            }
        } catch (Exception e) {
            logger.error("searchProvinceCity:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
        return restResult;
    }
}

