/*
 * Copyright (C) 2025 AxionOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.systemui.shared.clocks.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import com.android.systemui.customization.R
import com.android.systemui.shared.clocks.extensions.*
import kotlin.math.min

/**
 * A base class for font-based clocks that allows for granular per-digit positioning.
 */
abstract class FontDigitClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : NTClockView(context, attrs, defStyleAttr, defStyleRes) {

    protected val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }

    protected open val digitSpacing: Float get() = context.scaledDimen(R.dimen.clock_padding)
    protected open val topMargin: Float get() = 0f
    protected open val clockOffset: Float get() = 0f

    protected var isLarge: Boolean = false

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NTClockView)
        isLarge = ta.getBoolean(R.styleable.NTClockView_isLarge, false)
        ta.recycle()
    }

    abstract fun getCustomTypeface(): Typeface
    abstract fun getCustomTextSize(): Float

    protected open fun updatePaint() {
        textPaint.typeface = getCustomTypeface()
        textPaint.textSize = if (isLarge) getCustomTextSize() * 1.5f else getCustomTextSize()
        textPaint.color = clockColor
    }

    override fun refreshColor() {
        super.refreshColor()
        textPaint.color = clockColor
        invalidate()
    }

    override fun onFontSettingChanged() {
        super.onFontSettingChanged()
        updatePaint()
        requestLayout()
        invalidate()
    }

    protected open fun getCustomDigitXOffset(time: String, index: Int): Float = 0f
    protected open fun getCustomDigitYOffset(time: String, index: Int): Float = 0f

    protected open fun isVerticalStyle(): Boolean = false

    override fun drawClock(canvas: Canvas) {
        updatePaint()
        val time = timeStr
        if (time.length < 4) return

        if (isVerticalStyle()) {
            drawVertical(canvas, time)
        } else {
            drawHorizontal(canvas, time)
        }
    }

    private fun drawHorizontal(canvas: Canvas, time: String) {
        val totalWidth = computeTotalWidth(time)
        var x = (width - totalWidth) / 2f
        val centerY = (height / 2f) + topMargin

        time.forEachIndexed { index, char ->
            val charStr = char.toString()
            val digitX = x + getCustomDigitXOffset(time, index)
            val digitY = centerY + (textPaint.textSize * 0.3f) + getCustomDigitYOffset(time, index) - clockOffset
            
            canvas.drawText(charStr, digitX, digitY, textPaint)
            
            x += textPaint.measureText(charStr)
            if (index < time.lastIndex) {
                x += digitSpacing
            }
        }
    }

    private fun drawVertical(canvas: Canvas, time: String) {
        val hours = time.substring(0, 2)
        val minutes = time.substring(2, 4)

        val centerX = width / 2f
        val centerY = height / 2f
        
        val hourWidth = textPaint.measureText(hours)
        val minWidth = textPaint.measureText(minutes)
        
        val lineSpacing = context.scaledDimen(R.dimen.clock_stacked_line_spacing)
        
        canvas.drawText(hours, centerX - hourWidth / 2f, centerY - lineSpacing / 2f, textPaint)
        
        canvas.drawText(minutes, centerX - minWidth / 2f, centerY + lineSpacing / 2f + textPaint.textSize * 0.8f, textPaint)
    }

    private fun computeTotalWidth(time: String): Float {
        var total = 0f
        time.forEachIndexed { index, char ->
            total += textPaint.measureText(char.toString())
            total += getCustomDigitXOffset(time, index)
            if (index < time.lastIndex) {
                total += digitSpacing
            }
        }
        return total
    }
}
