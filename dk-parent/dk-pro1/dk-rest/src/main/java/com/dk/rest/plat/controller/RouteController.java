package com.dk.rest.plat.controller;

import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.controller.BaseController;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.rest.plat.bean.RouteInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * 查询通道信息（快捷，代还）
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

    //public RestResult
}
