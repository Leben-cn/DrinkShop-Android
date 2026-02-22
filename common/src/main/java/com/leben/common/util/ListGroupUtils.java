package com.leben.common.util;

import com.leben.common.model.bean.GroupEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListGroupUtils {
    /**
     * 定义一个接口，告诉工具类怎么从 Item 中获取分组的 Key
     */
    public interface GroupKeySelector<T, K> {
        K getKey(T item);
    }

    /**
     * 原有的方法：保持插入顺序
     */
    public static <T, K> List<GroupEntity<K, T>> groupList(List<T> sourceList, GroupKeySelector<T, K> selector) {
        return groupList(sourceList, selector, null);
    }

    /**
     * 【新增重载方法】：支持根据 Key 进行排序
     * @param keyComparator 传入分组 Key 的比较器，如果为 null 则保持原始顺序
     */
    public static <T, K> List<GroupEntity<K, T>> groupList(
            List<T> sourceList,
            GroupKeySelector<T, K> selector,
            Comparator<K> keyComparator) {

        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 分组逻辑（依然使用 LinkedHashMap 临时存储）
        Map<K, List<T>> map = new LinkedHashMap<>();
        for (T item : sourceList) {
            K key = selector.getKey(item);
            if (key == null) continue;

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(item);
        }

        // 2. 转换为 List<GroupEntity>
        List<GroupEntity<K, T>> result = new ArrayList<>();
        for (Map.Entry<K, List<T>> entry : map.entrySet()) {
            result.add(new GroupEntity<>(entry.getKey(), entry.getValue()));
        }

        // 3. 根据传入的比较器对“组”进行排序
        if (keyComparator != null) {
            // GroupEntity::getGroup 获取的是 ShopCategoriesEntity 对象
            result.sort((o1, o2) -> keyComparator.compare(o1.getHeader(), o2.getHeader()));
        }

        return result;
    }
}
