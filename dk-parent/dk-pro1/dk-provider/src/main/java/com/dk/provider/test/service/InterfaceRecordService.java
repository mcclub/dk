package com.dk.provider.test.service;

import com.dk.provider.test.entity.InterfaceRecordEntity;

import java.util.List;
import java.util.Map;

public interface InterfaceRecordService {
    /**
     * 查询
     * @param map 参数map
     * @return 返回List集合
     * @throws Exception
     */
    List<InterfaceRecordEntity> queryList(Map map) throws Exception;
    /**
     * 添加
     * @param replace 参数实体
     * @return 返回主键id
     * @throws Exception
     */
    int insert(InterfaceRecordEntity replace) throws Exception;

    /**
     * 修改
     * @param replace
     * @return
     * @throws Exception
     */
    int update(InterfaceRecordEntity replace) throws Exception;
}
