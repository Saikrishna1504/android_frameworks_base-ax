/*
 * Copyright (C) 2025 - 2026 AxionOS Project
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
import android.util.AttributeSet
import com.android.systemui.customization.R

class NTStencilClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : NTClockView(context, attrs, defStyleAttr, defStyleRes) {

    private val timePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private val dateAreaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    override fun getTag(): String = if (isLarge) "NTStencilLargeClockView" else "NTStencilClockView"

    private var isLarge: Boolean = false

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NTClockView)
        isLarge = ta.getBoolean(R.styleable.NTClockView_isLarge, false)
        ta.recycle()
        updatePaints()
    }

    private fun updatePaints() {
        val stencilTypeface = Typeface.create("Stencil-Regular", Typeface.NORMAL)
        timePaint.typeface = stencilTypeface
        timePaint.textSize = context.resources.getDimension(R.dimen.clock_stencil_text_size) * (if (isLarge) 1.5f else 1f)
        timePaint.color = clockColor

        dateAreaPaint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        dateAreaPaint.textSize = context.resources.getDimension(R.dimen.clock_stencil_date_size) * (if (isLarge) 1.2f else 1f)
        dateAreaPaint.color = clockColor
        dateAreaPaint.alpha = (255 * 0.9).toInt()
    }

    override fun refreshColor() {
        super.refreshColor()
        timePaint.color = clockColor
        dateAreaPaint.color = clockColor
        dateAreaPaint.alpha = (255 * 0.9).toInt()
        invalidate()
    }

    override fun onFontSettingChanged() {
        super.onFontSettingChanged()
        updatePaints()
        requestLayout()
        invalidate()
    }

    override fun drawClock(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f

        val dateText = dateStr
        val dateY = centerY - (timePaint.textSize / 4f)
        canvas.drawText(dateText, centerX, dateY, dateAreaPaint)

        val timeText = if (format?.contains(":") == true) timeStr else {
            if (timeStr.length == 4) "${timeStr.substring(0, 2)}:${timeStr.substring(2, 4)}" else timeStr
        }
        val timeY = centerY + (timePaint.textSize / 3f)
        canvas.drawText(timeText, centerX, timeY, timePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val timeHeight = timePaint.textSize
        val dateHeight = dateAreaPaint.textSize
        val totalHeight = (timeHeight + dateHeight * 2).toInt()
        setMeasuredDimension(width, totalHeight)
    }
}
