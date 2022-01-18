package com.vcg.mybatis.example.processor.domain;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int page;

    private int size;

    private long total;

    private int pages;

    private List<T> data;

    private PageInfo(){};

    public PageInfo(int page, int size, long total, List<T> data) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.data = data;
        if (total == 0) {
            this.pages = 0;
        } else {
            this.pages = (int) ((total / size) + ((total % size) > 0 ? 1 : 0));
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", pages=" + pages +
                ", data=" + data +
                '}';
    }
}
