package com.common.bean;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> extends RestResult implements Serializable {
    private List<T> records;
    private Integer total;
    private Integer size;
    private Integer pages;
    private Integer current;
    private boolean searchCount;
    private boolean optimizeCount;
    private String orderByField;
    private boolean isAsc;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public boolean isOptimizeCount() {
        return optimizeCount;
    }

    public void setOptimizeCount(boolean optimizeCount) {
        this.optimizeCount = optimizeCount;
    }

    public String getOrderByField() {
        return orderByField;
    }

    public void setOrderByField(String orderByField) {
        this.orderByField = orderByField;
    }

    public boolean isAsc() {
        return isAsc;
    }

    public void setAsc(boolean asc) {
        isAsc = asc;
    }

    public PageResult(List<T> records, Integer total, Integer size, Integer pages, Integer current, boolean searchCount, boolean optimizeCount, String orderByField, boolean isAsc) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.pages = pages;
        this.current = current;
        this.searchCount = searchCount;
        this.optimizeCount = optimizeCount;
        this.orderByField = orderByField;
        this.isAsc = isAsc;
    }

    public PageResult () {

    }
}
