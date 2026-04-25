package com.leben.common.util;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * 自定义基线偏移 Span，用于让符号“浮”起来
 */
public class CustomBaselineShiftSpan extends MetricAffectingSpan {
    private final float shiftRatio; // 偏移比例，正数向上，负数向下

    public CustomBaselineShiftSpan(float shiftRatio) {
        this.shiftRatio = shiftRatio;
    }

    @Override
    public void updateMeasureState(TextPaint tp) {
        tp.baselineShift += (int) (tp.ascent() * shiftRatio);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.baselineShift += (int) (tp.ascent() * shiftRatio);
    }
}