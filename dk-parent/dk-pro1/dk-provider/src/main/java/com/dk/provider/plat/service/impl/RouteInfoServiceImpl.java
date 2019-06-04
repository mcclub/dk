package com.dk.provider.plat.service.impl;

import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.common.utils.StringUtil;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.DetailRouteInfo;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.entity.RouteUser;
import com.dk.provider.plat.entity.UserRouteinfo;
import com.dk.provider.plat.mapper.RouteInfoMapper;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.user.entity.CardInfo;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.mapper.UserMapper;
import com.dk.provider.user.service.IUserService;
import edu.emory.mathcs.backport.java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("routeInfoService")
public class RouteInfoServiceImpl extends BaseServiceImpl<RouteInfo> implements RouteInfoService {

    private Logger logger = LoggerFactory.getLogger(RouteInfoServiceImpl.class);
    @Resource
    private RouteInfoMapper routeInfoMapper;
    @Resource
    public void setSqlMapper (RouteInfoMapper routeInfoMapper)
    {
        super.setBaseMapper (routeInfoMapper);
    }

    @Resource
    private IUserService userService;

    @Resource
    private UserMapper userMapper;
    /**
     * 分页条件查询  根据用户id userId 查询等级id 对应等级费率
     * @param map
     * @param pageable
     * @return
     * @throws Exception
     */
    @Override
    public Page<RouteInfo> page(Map map, Pageable pageable) throws Exception {
        /**
         * 根据用户id查询用户等级id
         */
        User user = userService.queryByid(Long.valueOf(map.get("userId").toString()));

        map.put("classId",user.getClassId());
        Page<RouteInfo> pages = super.findPages(map,pageable);
        return pages;
    }

    /**
     * 根据用户id userId 查询等级id 对应等级费率  并查出绑定储蓄卡
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public RouteUser queryUserRout(Map map) throws Exception {
        logger.info("进入queryUserRout的参数map:{}",map);
        Long userId = Long.valueOf(map.get("userId").toString());
        String routId = map.get("routId").toString();
        /**
         * 根据用户id查询用户等级id
         */
        User user = userService.queryByid(userId);
        map.put("classId",user.getClassId());
        List<RouteInfo> routeInfoList = super.query(map);
        /**
         * 用户id查询绑定储蓄卡
         */
        Map<String,Object> map1 = new HashMap<>();
        map1.put("userId",userId);
        map1.put("type","01");
        CardInfo cardInfo = userService.queryCard(map1);
        RouteUser routeUser = new RouteUser();
        if(routeInfoList.size() >0){
            routeUser.setUserId(user.getId().toString());
            routeUser.setRoutId(routId);
            routeUser.setRate(routeInfoList.get(0).getRate());
            routeUser.setFee(routeInfoList.get(0).getFee());
            routeUser.setSettleCard(cardInfo.getCardCode());
            routeUser.setRealName(cardInfo.getRealName());
        }

        return routeUser;
    }

    @Override
    public RestResult routeInfoByUser(Map map) {
        RestResult restResult = new RestResult();
        Map<String,Object> resultMap = new HashMap<>();
        List<DetailRouteInfo> userRouteinfo = routeInfoMapper.routeInfoByUser(map);
        if (userRouteinfo != null && userRouteinfo.size() > 0) {
            resultMap.put("classId",userRouteinfo.get(0).getClassId());
            resultMap.put("className",userRouteinfo.get(0).getClassName());
        }
        resultMap.put("info",userRouteinfo);
        restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",resultMap);
        return restResult;
    }

    @Override
    public RestResult parentRouteInfo(Map map) {
        RestResult restResult = new RestResult();
        Long classId = userMapper.searchClassId(map);
        if (StringUtil.isNotEmpty(classId)) {
            if (!classId.equals(6l)) {
                map.put("classId",classId+1);
                Map<String,Object> resultMap = new HashMap<>();
                List<DetailRouteInfo> userRouteinfo = routeInfoMapper.parentRouteInfo(map);
                if (userRouteinfo != null && userRouteinfo.size() > 0) {
                    resultMap.put("classId",userRouteinfo.get(0).getClassId());
                    resultMap.put("className",userRouteinfo.get(0).getClassName());
                }
                resultMap.put("info",userRouteinfo);
                return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",resultMap);
            } else {
                return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户等级已为最高");
            }
        } else {
            return restResult.setCodeAndMsg(ResultEnume.FAIL,"用户等级查询失败");
        }
    }

    /**
     * 根据用户查询通道等级费率
     * @param map
     * @return
     * @throws Exception
     */
    @Override
    public List<RouteInfo> findUserrate(Map map){
        /**
         * 根据用户id查询用户等级id
         * 大类通道id
         */
        if(map != null){
            Long userId = (Long) map.get("userId");
            User user = userMapper.queryByid(userId);
            if(user != null){
                map.put("classId",user.getClassId());
                return  routeInfoMapper.query(map);
            }
        }
        return null;
    }
}
