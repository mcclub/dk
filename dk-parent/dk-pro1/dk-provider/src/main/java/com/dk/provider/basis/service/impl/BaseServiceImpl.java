package com.dk.provider.basis.service.impl;

import com.common.bean.page.Page;
import com.common.bean.page.Pageable;
import com.dk.provider.basis.mapper.BaseMapper;
import com.dk.provider.basis.service.BaseServiceI;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
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

    public T queryByid(Long id) {
        return this.baseMapper.queryByid(id);
    }

    public Page<T> findPages(Map<String, Object> params, Pageable pageable) {
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

        long total = (long)this.baseMapper.counts((Map)params);
        this.setParams((Map)params, (int)total, pageable);
        return new Page(this.baseMapper.query((Map)params), total, pageable);
    }
    protected void setParams(Map<String, Object> params, int total, Pageable pageable) {
        if (params == null) {
            params = new HashMap();
        }

        int totalPages = (int)Math.ceil((double)total / (double)pageable.getPageSize());
        if (totalPages < pageable.getPageNumber()) {
            pageable.setPageNumber(totalPages);
        }

        int firstResult = (pageable.getPageNumber() - 1) * pageable.getPageSize();
        int maxResults = pageable.getPageSize();
        if (StringUtils.hasText(pageable.getOrderProperty()) && pageable.getOrderDirection() != null) {
            StringBuilder odr = new StringBuilder();
            odr.append(pageable.getOrderProperty()).append(" ").append(pageable.getOrderDirection().toString());
            ((Map)params).put("order by", odr.toString());
        }

        ((Map)params).put("start", new Long((long)firstResult));
        ((Map)params).put("max", new Long((long)maxResults));
    }
}
