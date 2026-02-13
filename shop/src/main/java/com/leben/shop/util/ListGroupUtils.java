package com.leben.shop.util;

import com.leben.shop.model.bean.GroupEntity;

import java.util.ArrayList;
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
     * 通用分组方法
     * @param sourceList 原始扁平数据 (如 List<DrinksEntity>)
     * @param selector   分组器 (告诉工具类按什么字段分组)
     * @param <T>        数据类型 (DrinksEntity)
     * @param <K>        分组Key的类型 (CategoriesEntity 或 String)
     * @return 分组后的有序列表
     */
    public static <T, K> List<GroupEntity<K, T>> groupList(List<T> sourceList, GroupKeySelector<T, K> selector) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用 LinkedHashMap 保持插入顺序
        Map<K, List<T>> map = new LinkedHashMap<>();

        for (T item : sourceList) {
            K key = selector.getKey(item);
            if (key == null) continue;

            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(item);
        }

        // 将 Map 转回 List<GroupBean>
        List<GroupEntity<K, T>> result = new ArrayList<>();
        for (Map.Entry<K, List<T>> entry : map.entrySet()) {
            result.add(new GroupEntity<>(entry.getKey(), entry.getValue()));
        }

        return result;
    }
}
