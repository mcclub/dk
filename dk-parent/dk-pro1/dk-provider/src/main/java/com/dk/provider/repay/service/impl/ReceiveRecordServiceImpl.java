package com.dk.provider.repay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.bean.RestResult;
import com.common.bean.ResultEnume;
import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.common.utils.CommonUtils;
import com.dk.provider.basis.service.impl.BaseServiceImpl;
import com.dk.provider.plat.entity.RouteInfo;
import com.dk.provider.plat.service.RouteInfoService;
import com.dk.provider.repay.entity.ReceiveHistory;
import com.dk.provider.repay.entity.ReceiveRecord;
import com.dk.provider.repay.mapper.ReceiveRecordMapper;
import com.dk.provider.repay.service.ReceiveRecordService;
import com.dk.provider.user.entity.User;
import com.dk.provider.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("receiveRecordService")
public class ReceiveRecordServiceImpl extends BaseServiceImpl<ReceiveRecord> implements ReceiveRecordService {

    private Logger logger = LoggerFactory.getLogger(ReceiveRecordServiceImpl.class);
    @Resource
    private ReceiveRecordMapper receiveRecordMapper;
    @Resource
    public void setSqlMapper (ReceiveRecordMapper receiveRecordMapper)
    {
        super.setBaseMapper (receiveRecordMapper);
    }

    @Resource
    private RouteInfoService routeInfoService;

    @Resource
    private UserMapper userMapper;


    /**
     * 新增快捷订单 关联用户、费率信息
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrder(JSONObject jsonObject) throws Exception {
        Long userId = jsonObject.getLong("userId");//平台用户id
        String routId = jsonObject.getString("routId");//大类通道id

        /**
         * 根据用户id查询费率单笔手续费 ,到账储蓄卡
         */
        Map<String ,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("routId",routId);




        /**
         * 自动生成订单号
         */
        String orderNo = CommonUtils.getOrderNo();
        ReceiveRecord receiveRecord = new ReceiveRecord();
        receiveRecord.setOrderNo(orderNo);
        receiveRecord.setUserId(userId.toString());


        return receiveRecordMapper.insert(receiveRecord);
    }

    /**
     * 修改订单状态和描述
     * @param receiveRecord
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStates(ReceiveRecord receiveRecord) throws Exception {
        return receiveRecordMapper.updateStates(receiveRecord);
    }


    /**
     * 查询收款纪录
     * @param map
     * @param pageable
     * @return
     */
    @Override
    public RestResult pageHistory(Map map, Pageable pageable) {
        RestResult restResult = new RestResult();
        Page<ReceiveRecord> page = this.findPage(map,pageable);
        if (page.getTotal() > 0) {
            List<ReceiveHistory> list = new ArrayList<>();
            for (ReceiveRecord receiveRecord : page.getContent()) {
                ReceiveHistory history = new ReceiveHistory();
                history.setId(receiveRecord.getId());
                history.setUserId(receiveRecord.getUserId());
                history.setAmount(receiveRecord.getAmount());
                history.setOrderNo(receiveRecord.getOrderNo());
                history.setReceCard(receiveRecord.getReceCard());
                history.setStates(receiveRecord.getStates());
                history.setCreateTime(receiveRecord.getCreateTime());
                list.add(history);
            }
            Page<ReceiveHistory> recordPage = new Page<>(list,page.getTotal(),pageable);
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",recordPage);
        } else {
            return restResult.setCodeAndMsg(ResultEnume.SUCCESS,"查询成功",new Page<ReceiveHistory>());
        }
    }


    public Page<ReceiveRecord> findPage(Map<String, Object> params, Pageable pageable) {
        if (params == null || ((Map)params).isEmpty()) {
            params = new HashMap();
        }

        if (pageable == null) {
            pageable = new Pageable();
        } else {
            String searchProperty = pageable.getSearchProperty();
            String searchValue = pageable.getSearchValue();
            if (StringUtils.hasText(searchProperty) && StringUtils.hasText(searchValue)) {
                ((Map)params).put(searchProperty, searchValue);
            }
        }

        long total = (long)receiveRecordMapper.historyCounts((Map)params);
        this.setParams((Map)params, (int)total, pageable);
        return new Page(receiveRecordMapper.historyQuery((Map)params), total, pageable);
    }


    /**
     * 修改订单状态和 新增返佣记录
     * @param jsonObject
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReceiveOrder(JSONObject jsonObject) {
        String orderNo = jsonObject.getString("orderNo");
        String states = jsonObject.getString("states");
        String respMsg = jsonObject.getString("respMsg");

        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        List<ReceiveRecord> receiveRecordList = receiveRecordMapper.query(map);
        if(receiveRecordList.size() >0) {
            if (!"1".equals(receiveRecordList.get(0).getStates())) {
                /**
                 * 修改订单状态和描述
                 */
                ReceiveRecord updaRece = new ReceiveRecord();
                updaRece.setOrderNo(orderNo);
                updaRece.setStates(Long.valueOf(states));
                updaRece.setOrderDesc(respMsg);
                int updat = receiveRecordMapper.updateStates(updaRece);

                /**
                 * 订单成功 计算返佣 并新增 返佣记录
                 */
                if ("1".equals(states)) {
                    //查询当前人的通道费率和等级
                    String routeId = receiveRecordList.get(0).getRouteId();//大类通道id
                    String userId = receiveRecordList.get(0).getUserId();//用户id
                    String rate = receiveRecordList.get(0).getRate();//刷卡时的费率

                    User user = userMapper.queryByid(Long.valueOf(userId));
                    if (user != null) {
                        Long classId = user.getClassId();//该用户的等级id
                    }
                    /**
                     * 查询用户等级等级类型(1普通等级,2代理商等级)
                     * 普通等级返给上一级用户
                     * 代理商等级返给上两级用户
                     */


                    /**
                     s* 查看用户上级 并查出上级的等级费率
                     */
                }
            }
        }

     return 0;
    }

}
