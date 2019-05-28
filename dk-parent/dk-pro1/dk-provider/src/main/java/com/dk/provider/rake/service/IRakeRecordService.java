package com.dk.provider.rake.service;


import com.common.bean.RestResult;
import com.dk.provider.basis.service.BaseServiceI;
import com.dk.provider.rake.entity.RakeRecord;

import java.util.List;
import java.util.Map;

/**
 * 返佣记录
 */
public interface IRakeRecordService extends BaseServiceI<RakeRecord> {
    /**
     * 计算返佣
     * @param map
     * @return
     */
    double countRebate (Map map);

    /**
     * 查询返佣
     * @param map
     * @return
     */
    RestResult queryRebate(Map map);

    /**
     * 用户返佣
     * @param map
     * @return
     */
    double userRebate (Map map);


    /**
     * 是否为vip
     * @return
     */
    boolean isVip (Map map);

    /**
     * 好友列表
     * @return
     */
    RestResult friendList (Map map);
}
