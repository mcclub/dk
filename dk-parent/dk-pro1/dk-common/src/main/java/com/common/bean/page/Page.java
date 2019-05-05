package com.common.bean.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {
    private static final long serialVersionUID = -2053800594583879853L;
    private final List<T> content = new ArrayList();
    private final long total;
    private final Pageable pageable;
    public Page() {
        this.total = 0L;
        this.pageable = new Pageable();
    }

    public Page(Pageable pageable) {
        this.total = 0L;
        this.pageable = pageable;
    }

    public Page(List<T> content, long total, Pageable pageable) {
        this.content.addAll(content);
        this.total = total;
        this.pageable = pageable;
    }

    public int getPageNumber() {
        return this.pageable.getPageNumber();
    }

    public int getPageSize() {
        return this.pageable.getPageSize();
    }

    public int getTotalPages() {
        return (int)Math.ceil((double)this.getTotal() / (double)this.getPageSize());
    }

    public List<T> getContent() {
        return this.content;
    }

    public long getTotal() {
        return this.total;
    }

    public Pageable getPageable() {
        return this.pageable;
    }
}
