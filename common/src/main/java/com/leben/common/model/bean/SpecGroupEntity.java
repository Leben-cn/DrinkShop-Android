package com.leben.common.model.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * 分组的 Key 对象
 * 用来保存组的元数据：组ID、组名、单选/多选状态
 */
public class SpecGroupEntity implements Serializable {

    public Long groupId;
    public String groupName;
    public boolean isMultiple; // 1=true, 0/null=false

    // 构造函数：用于在分组工具中创建 Key
    public SpecGroupEntity(Long groupId, String groupName, Integer isMultiple) {
        this.groupId = groupId;
        this.groupName = groupName;
        // 处理 null 安全，防止空指针
        this.isMultiple = (isMultiple != null && isMultiple == 1);
    }

    // 必须重写 equals 和 hashCode，仅比较 groupId
    // 只要 groupId 相同，就认为是同一组
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecGroupEntity that = (SpecGroupEntity) o;
        return Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }
}