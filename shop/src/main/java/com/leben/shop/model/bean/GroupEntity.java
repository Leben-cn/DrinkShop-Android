package com.leben.shop.model.bean;

import java.util.List;

/**
 * 通用分组实体
 * @param <K> 分组的依据（Header），比如 CategoryEntity，或者 String 类型的标题
 * @param <V> 列表里的具体数据（Item），比如 DrinksEntity
 */
public class GroupEntity<K, V> {
    private K header;       // 这一组的标题/分类信息
    private List<V> children; // 这一组包含的所有数据

    public GroupEntity(K header, List<V> children) {
        this.header = header;
        this.children = children;
    }

    public K getHeader() { return header; }
    public List<V> getChildren() { return children; }
}