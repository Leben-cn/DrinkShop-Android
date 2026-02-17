package com.leben.shop.model.bean;

public class SpecGroupKey {
    public Long groupId;
    public String groupName;
    public boolean isMultiple; // 是否多选

    public SpecGroupKey(Long groupId, String groupName, int isMultiple) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.isMultiple = (isMultiple == 1);
    }

    // 【重要】必须重写 equals 和 hashCode，否则 Map 无法正确去重分组
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecGroupKey that = (SpecGroupKey) o;
        return groupId.equals(that.groupId);
    }

    @Override
    public int hashCode() {
        return groupId.hashCode();
    }
}
