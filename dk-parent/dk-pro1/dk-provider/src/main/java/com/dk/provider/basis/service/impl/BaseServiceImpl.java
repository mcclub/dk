package com.dk.provider.basis.service.impl;

import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.basis.service.BaseServiceI;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class BaseServiceImpl<T> implements Serializable , BaseServiceI<T> {

    public BaseServiceImpl(){

    }

    private BaseMapper<T> baseMapper;

    protected void setBaseMapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public List<T> query(Map map) throws Exception {
        return this.baseMapper.query(map);
    }

    public int insert(T t) throws Exception {
        return this.baseMapper.insert(t);
    }

    public int update(T t) throws Exception {
        return this.baseMapper.update(t);
    }

    public void delete(Long id) throws Exception {
        this.baseMapper.delete(id);
    }
}
