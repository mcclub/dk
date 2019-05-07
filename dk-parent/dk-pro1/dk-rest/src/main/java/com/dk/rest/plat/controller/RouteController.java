package com.dk.rest.plat.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.controller.BaseController;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.Subchannel;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.plat.service.SubchannelService;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.rest.plat.bean.RouteInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
            Page<RouteInfo> routeInfoPage = routeInfoService.page(map,pageable);
            Page<RouteInfoBean> page = new Page<>(new LinkedList<>(), routeInfoPage.getTotal(), pageable);
            List<RouteInfoBean> routeInfoBeanList = new LinkedList<>();
            if(routeInfoBeanList != null){
                for(RouteInfo routeInfo : routeInfoPage.getContent() ){
                    RouteInfoBean routeInfoBean = new RouteInfoBean();
                    BeanUtils.copyProperties(routeInfo,routeInfoBean);
                    routeInfoBeanList.add(routeInfoBean);
                }
                logger.info("返回routeInfoBeanList：{}",routeInfoBeanList);
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
             * 订单金额
             * 交易通道id
             * 用户id
             * 费率
             * 单笔
             * 交易卡号
             * 到账卡号
             * 可选(省份，城市，行业，商户)后面拓展
             */
            ReceiveRecord receiveRecord = new ReceiveRecord();

            receiveRecordService.insert(receiveRecord);

            /**
             * 随机路由小类通道
             */
            Map<String,Object> map = new HashMap<>();
            map.put("routId","");//大类通道id
            List<Subchannel> subchannelList = subchannelService.selectByrout(map);
            if(subchannelList.size() >0){
                /**
                 * 随机取一条
                 */
                Random random = new Random();
                int n = random.nextInt(subchannelList.size());//随机数
                Subchannel subchannel = subchannelList.get(n-1);//取出的随机一条数据
                logger.info("随机取出的小类subchannel:{}",subchannel);

                String tabNo = subchannel.getTabNo();//小类通道标记
                Long subId = subchannel.getId();//小类通道id



            }else{
                return getRestResult(ResultEnume.FAIL,"暂无可用路由通道",null);
            }
            return null;
        }catch (Exception e){
            logger.error(method+"执行出错:{}",e.getMessage());
            e.printStackTrace();
            return getFailRes();
        }
    }
}
