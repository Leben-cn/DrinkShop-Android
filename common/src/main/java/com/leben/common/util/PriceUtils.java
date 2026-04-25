package com.leben.common.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PriceUtils {
    private static final DecimalFormat df = new DecimalFormat("0.##");

    /**
     * 核心格式化方法
     * @param price 价格
     * @param hasSpecs 是否有规格（显示“起”）
     * @param sizeRatio 符号缩放比例
     * @param isHighlightSuffix 是否高亮“起”字
     * @param needSpace 符号 ¥ 和价格之间是否需要空格
     */
    public static CharSequence formatPrice(BigDecimal price, boolean hasSpecs, float sizeRatio, boolean isHighlightSuffix, boolean needSpace) {
        // 1. 格式化数字：强制保留两位小数会比 "0.##" 更有商业质感
        DecimalFormat strongDf = new DecimalFormat("0.##");
        String priceStr = (price == null) ? "0.00" : strongDf.format(price);

        String symbol = needSpace ? "¥ " : "¥";
        String suffix = hasSpecs ? " 起" : "";
        String fullText = symbol + priceStr + suffix;

        SpannableStringBuilder builder = new SpannableStringBuilder(fullText);

        // --- 处理 ¥ 符号 ---
        int symbolEnd = symbol.length();
        builder.setSpan(new RelativeSizeSpan(sizeRatio), 0, symbolEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new CustomBaselineShiftSpan(0.15f), 0, symbolEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 符号用普通粗细，衬托后面的数字
        builder.setSpan(new TypefaceSpan("sans-serif"), 0, symbolEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // --- 处理数字部分 (核心：让它最粗) ---
        int priceStart = symbolEnd;
        int priceEnd = symbolEnd + priceStr.length();
        // 使用系统内置最粗的 Black 变体 (部分 5.0+ 设备支持) 或直接加粗
        builder.setSpan(new TypefaceSpan("sans-serif-black"), priceStart, priceEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), priceStart, priceEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // --- 处理“起”字 ---
        if (hasSpecs) {
            int start = fullText.indexOf(suffix);
            int end = fullText.length();
            builder.setSpan(new RelativeSizeSpan(0.6f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new TypefaceSpan("sans-serif-normal"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            String colorCode = isHighlightSuffix ? "#ed3107" : "#0a0700";
            builder.setSpan(new ForegroundColorSpan(Color.parseColor(colorCode)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }

    /**
     * 新增重载：支持控制空格和高亮
     */
    public static CharSequence formatPrice(BigDecimal price, boolean hasSpecs, boolean isHighlightSuffix, boolean needSpace) {
        return formatPrice(price, hasSpecs, 0.7f, isHighlightSuffix, needSpace);
    }

    /**
     * 保持原有兼容性的重载：默认不带空格
     */
    public static CharSequence formatPrice(BigDecimal price, boolean hasSpecs, boolean isHighlightSuffix) {
        return formatPrice(price, hasSpecs, 0.7f, isHighlightSuffix, false);
    }

    public static CharSequence formatPrice(BigDecimal price, boolean hasSpecs) {
        return formatPrice(price, hasSpecs, 0.7f, false, false);
    }
}