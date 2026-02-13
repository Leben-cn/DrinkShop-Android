package com.leben.shop.model.bean;

import java.util.List;

public class PageEntity<T> {

    private List<T> content;
    private boolean last;      // 是否最后一页
    private int totalPages;    // 总页数
    private long totalElements; // 总条数

    public List<T> getContent() { return content; }
    public boolean isLast() { return last; }
}