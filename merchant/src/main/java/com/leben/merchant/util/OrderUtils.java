package com.leben.merchant.util;

import com.leben.common.model.bean.OrderEntity;
import java.math.BigDecimal;
import java.util.List;

public class OrderUtils {
    /**
     * 统计订单
     * @return Object数组：[0]是Integer(笔数), [1]是BigDecimal(总金额)
     */
    public static Object[] getStats(List<OrderEntity> data) {
        if (data == null || data.isEmpty()) {
            return new Object[]{0, BigDecimal.ZERO};
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderEntity order : data) {
            if (order.getPayAmount() != null) {
                total = total.add(order.getPayAmount());
            }
        }
        return new Object[]{data.size(), total};
    }
}