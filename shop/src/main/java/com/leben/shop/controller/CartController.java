package com.leben.shop.controller;

import com.leben.shop.model.bean.CartEntity;
import com.leben.shop.model.bean.DrinkEntity;
import com.leben.common.model.bean.OrderItemEntity;
import com.leben.shop.model.bean.SpecOptionEntity;
import com.leben.shop.model.event.CartEvent;
import org.greenrobot.eventbus.EventBus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartController {

    private static volatile CartController instance;

    // 使用 LinkedHashMap 保证加入购物车的顺序
    private final Map<String, CartEntity> cartItems = new LinkedHashMap<>();

    // 新增：记录当前购物车的归属店铺ID
    private long currentShopId = -1;

    private CartController() {}

    public static CartController getInstance() {
        if (instance == null) {
            synchronized (CartController.class) {
                if (instance == null) {
                    instance = new CartController();
                }
            }
        }
        return instance;
    }

    /**
     * 进入店铺时调用此方法检查
     * @param newShopId 新进入的店铺ID
     */
    public void checkShopId(long newShopId) {
        if (currentShopId != -1 && currentShopId != newShopId) {
            // 如果之前有店铺ID，且和现在的不一样 -> 清空旧数据
            clear();
        }
        // 更新为当前店铺ID
        this.currentShopId = newShopId;
    }

    /**
     * 【添加商品】支持带规格
     * @param drink 商品本体
     * @param specs 选中的规格列表 (可以为null)
     * @param finalPrice 最终单价 (基础价+规格价)
     * @param specDesc 规格描述文本 (如"少冰")
     */
    public void add(DrinkEntity drink, List<SpecOptionEntity> specs, BigDecimal finalPrice, String specDesc) {
        if (drink == null) return;

        // 1. 生成唯一 Key
        String key = generateKey(drink, specs);

        CartEntity item = cartItems.get(key);
        if (item == null) {
            // 新组合，创建新条目
            item = new CartEntity(drink, 1, specs, finalPrice, specDesc);
            cartItems.put(key, item);
        } else {
            // 已存在完全一样的组合，数量+1
            item.setQuantity(item.getQuantity() + 1);
        }
        notifyChange();
    }

    public void add(DrinkEntity drink) {
        // 没有规格时，单价就是原价，规格描述为空
        add(drink, null, drink.getPrice(), "");
    }

    /**
     * 减少商品
     */
    public void remove(DrinkEntity drink) {
        // 默认移除无规格版本
        String key = generateKey(drink, null);
        CartEntity item = cartItems.get(key);
        if (item != null) {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity <= 0) {
                cartItems.remove(key);
            } else {
                item.setQuantity(newQuantity);
            }
            notifyChange();
        }
    }

    /**
     * 【重载方法】移除购物车条目（支持带规格）
     * 对应购物车弹窗里的减号点击事件
     */
    public void remove(CartEntity cartEntity) {
        if (cartEntity == null) return;

        // 1. 核心步骤：从传入的实体中拿出 Drink 和 Specs，重新生成唯一的 Key
        // 这样才能精准找到是哪一个“规格组合”
        String key = generateKey(cartEntity.getDrink(), cartEntity.getSpecs());

        // 2. 从 Map 中获取真实的数据对象
        CartEntity existingItem = cartItems.get(key);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() - 1;
            if (newQuantity <= 0) {
                // 3. 如果数量归零，直接从 Map 中移除该 Key
                cartItems.remove(key);
            } else {
                // 4. 否则只减少数量
                existingItem.setQuantity(newQuantity);
            }
            // 5. 发送通知，更新 UI
            notifyChange();
        }
    }

    /**
     * 获取购物车当前所有列表
     */
    public List<CartEntity> getCartList() {
        return new ArrayList<>(cartItems.values());
    }

    /**
     * 清空购物车
     */
    public void clear() {
        cartItems.clear();
        // currentShopId = -1; // 可选：清空后是否重置ID看业务需求，通常不需要
        notifyChange();
    }

    /**
     * 获取某个商品的当前数量（用于刷新列表UI）
     */
    public int getProductQuantity(long drinkId) {
        int total = 0;
        for (CartEntity item : cartItems.values()) {
            if (item.getDrink().getId().equals(drinkId)) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    /**
     * 获取总数量
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartEntity item : cartItems.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    /**
     * 获取总金额 (返回 BigDecimal 以保持精度)
     */
    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartEntity item : cartItems.values()) {
            // BigDecimal 必须使用 add 方法，且需要接收返回值（它是不可变对象）
            total = total.add(item.getItemTotalPrice());
        }
        return total;
    }

    /**
     * 发送通知
     */
    private void notifyChange() {
        // 使用 EventBus 发送事件
        // 任何注册了 @Subscribe 的地方（Activity/Fragment）都会收到这个事件
        EventBus.getDefault().post(new CartEvent(getTotalQuantity(), getTotalPrice()));
    }

    /**
     * 【核心算法】生成唯一 Key
     * 逻辑：商品ID + "_" + 排序后的规格ID
     */
    private String generateKey(DrinkEntity drink, List<SpecOptionEntity> specs) {
        StringBuilder sb = new StringBuilder();
        sb.append(drink.getId());

        if (specs != null && !specs.isEmpty()) {
            // 1. 复制列表防止影响原数据
            List<SpecOptionEntity> sortedSpecs = new ArrayList<>(specs);
            // 2. 排序 (依赖 SpecOptionEntity 实现 Comparable)
            Collections.sort(sortedSpecs);

            // 3. 拼接 ID
            for (SpecOptionEntity spec : sortedSpecs) {
                sb.append("_").append(spec.getId());
            }
        }
        return sb.toString();
    }

    /**
     * 【新增】计算总打包费
     * 逻辑：遍历购物车每一项，累加 (单品打包费 * 数量)
     */
    public BigDecimal getTotalPackingFee() {
        BigDecimal totalFee = BigDecimal.ZERO;

        for (CartEntity item : cartItems.values()) {
            DrinkEntity drink = item.getDrink();
            if (drink != null) {
                // 1. 获取单品打包费
                BigDecimal fee = drink.getPackingFee();

                // 2. 获取该项的数量
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

                // 3. 累加：总费 = 总费 + (单费 * 数量)
                totalFee = totalFee.add(fee.multiply(quantity));
            }
        }
        return totalFee;
    }

    // 在 CartController 类中添加这个新方法
    public List<OrderItemEntity> getOrderItemsForSubmit() {
        List<OrderItemEntity> orderItems = new ArrayList<>();

        // 遍历购物车中的所有 CartEntity
        for (CartEntity cartItem : getCartList()) {
            OrderItemEntity item = new OrderItemEntity();

            // --- 开始字段映射 (Mapping) ---
            // 左边是后端需要的 OrderItemEntity，右边是购物车里的 CartEntity

            item.setProductId(cartItem.getDrink().getId());
            item.setProductName(cartItem.getDrink().getName());
            item.setProductImg(cartItem.getDrink().getImg());
            item.setQuantity(cartItem.getQuantity());

            // 价格处理：确保类型一致，如果是 double 需要转 BigDecimal
            // 假设 cartItem.getPrice() 返回的是 double 或 BigDecimal
            item.setPrice(new BigDecimal(String.valueOf(cartItem.getCurrentPrice())));

            // 规格处理
            item.setSpecDesc(cartItem.getSpecDesc()); // 如 "少冰, 半糖"
            // 如果 CartEntity 里存了 specId，这里也赋值，没有就设为 null 或空串
            // item.setSpecIds(cartItem.getSpecIds());

            orderItems.add(item);
        }

        return orderItems;
    }
}