package com.dk.provider.basis.service;

import java.util.List;
import java.util.Map;

public interface BaseServiceI<T> {
    /**
     * 查询
     * @param map
     * @return
     */
    List<T> query(Map map) throws Exception;

    /**
     * 添加
     * @param t
     * @return
     */
    int insert(T t) throws Exception;

    /**
     * 修改
     * @param t
     * @return
     */
    int update(T t) throws Exception;

    /**
     * 删除
     * @param id
     */
    void delete(Long id) throws Exception;

    /**
     * 根据id查询
     * @param id
     * @return
     */
    T queryByid(Long id);
}
