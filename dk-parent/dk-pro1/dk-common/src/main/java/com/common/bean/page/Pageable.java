package com.common.bean.page;

import com.common.bean.page.Order.Direction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pageable implements Serializable {
    private static final long serialVersionUID = -3930180379790344299L;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private int pageNumber = 1;
    private int pageSize = 10;
    private String searchProperty;
    private String searchValue;
    private String orderProperty;
    private Direction orderDirection;
    private List<Order> orders = new ArrayList();

    public Pageable() {
    }

    public Pageable(Integer pageNumber, Integer pageSize) {
        if (pageNumber != null && pageNumber >= 1) {
            this.pageNumber = pageNumber;
        }

        if (pageSize != null && pageSize >= 1 && pageSize <= 100) {
            this.pageSize = pageSize;
        }

    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }

        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        this.pageSize = pageSize;
    }
    public String getSearchProperty() {
        return this.searchProperty;
    }

    public void setSearchProperty(String searchProperty) {
        this.searchProperty = searchProperty;
    }

    public String getSearchValue() {
        return this.searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getOrderProperty() {
        return orderProperty;
    }

    public void setOrderProperty(String orderProperty) {
        this.orderProperty = orderProperty;
    }

    public Direction getOrderDirection() {
        return this.orderDirection;
    }

    public void setOrderDirection(Direction orderDirection) {
        this.orderDirection = orderDirection;
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            Pageable other = (Pageable)obj;
            return (new EqualsBuilder()).append(this.getPageNumber(), other.getPageNumber()).append(this.getPageSize(), other.getPageSize()).append(this.getSearchProperty(), other.getSearchProperty()).append(this.getSearchValue(), other.getSearchValue()).append(this.getOrderProperty(), other.getOrderProperty()).append(this.getOrderDirection(), other.getOrderDirection()).append(this.getOrders(), other.getOrders()).isEquals();
        }
    }

    public int hashCode() {
        return (new HashCodeBuilder(17, 37)).append(this.getPageNumber()).append(this.getPageSize()).append(this.getSearchProperty()).append(this.getSearchValue()).append(this.getOrderProperty()).append(this.getOrderDirection()).append(this.getOrders()).toHashCode();
    }
}
