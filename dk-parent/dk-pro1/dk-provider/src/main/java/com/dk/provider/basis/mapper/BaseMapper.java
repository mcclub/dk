package com.dk.provider.basis.mapper;


import java.util.List;
import java.util.Map;

public interface BaseMapper<T> {
    /**
     * 查询
     * @param map
     * @return
     */
    List<T> query(Map map) ;

    /**
     * 添加
     * @param t
     * @return
     */
    int insert(T t);

    /**
     * 修改
     * @param t
     * @return
     */
    int update(T t);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 统计
     * @param map
     * @return
     */
    int counts(Map<String, Object> map)  ;

    /**
     * 分页查询
     * @param map
     * @return
     */
    List<T> finds(Map<String, Object> map);
}
