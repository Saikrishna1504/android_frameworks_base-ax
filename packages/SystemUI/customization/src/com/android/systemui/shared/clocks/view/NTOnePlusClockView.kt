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
import java.text.SimpleDateFormat
import java.util.*

/**
 * A horizontal clock view using Oplus typography.
 */
class NTOnePlusClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FontBitmapClockView(context, attrs, defStyleAttr, defStyleRes) {

    private val infoPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    override fun getTag(): String = if (isLarge) "NTOnePlusLargeClockView" else "NTOnePlusClockView"

    override fun getCustomTypeface(): Typeface = Typeface.create("OplusClock-Regular", Typeface.NORMAL)

    override fun getCustomTextSize(): Float = context.resources.getDimension(R.dimen.clock_oneplus_time_size)

    override val useSeparator: Boolean get() = true
    override val separatorType: SeparatorType get() = SeparatorType.DOTS

    private val weekSize get() = context.resources.getDimension(R.dimen.clock_oneplus_week_size)
    private val ampmSize get() = context.resources.getDimension(R.dimen.clock_oneplus_ampm_size)
    private val spacing get() = context.resources.getDimension(R.dimen.clock_oneplus_spacing)

    override fun shouldUseLightVariant(time: String, index: Int): Boolean {
        return time[index] == '1'
    }

    override fun drawClock(canvas: Canvas) {
        if (!bitmapsReady) loadBitmaps()
        val time = timeStr
        if (time.isEmpty()) return
        
        val scale = digitScale
        val totalWidth = computeTotalWidth(time, scale)
        val startX = (width - totalWidth) / 2f
        
        infoPaint.typeface = getCustomTypeface()
        infoPaint.color = clockColor
        infoPaint.textSize = if (isLarge) weekSize * 1.5f else weekSize
        infoPaint.textAlign = if (isLarge) Paint.Align.CENTER else Paint.Align.LEFT

        val week = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val date = SimpleDateFormat("MMMM d", Locale.getDefault()).format(calendar.time)
        
        val timeY = (height / 2f) + topMargin
        val weekY = timeY - (getCustomTextSize() * scale / 2f) - spacing
        val dateY = timeY + (getCustomTextSize() * scale / 2f) + spacing + infoPaint.textSize

        canvas.drawText(week, if (isLarge) width / 2f else startX, weekY, infoPaint)

        val whiteFilter = PorterDuffColorFilter(clockColor, PorterDuff.Mode.SRC_IN)
        val redFilter = PorterDuffColorFilter(if (isDoze || isScreenOff) Color.WHITE else context.getColor(R.color.oneplus_red), PorterDuff.Mode.SRC_IN)

        var x = startX
        time.forEachIndexed { index, char ->
            val bitmap = getBitmapForDigit(char) ?: return@forEachIndexed
            val isRed = char == '1'
            paint.colorFilter = if (isRed) redFilter else whiteFilter
            
            val yOffset = timeY - (bitmap.height * scale / 2)
            
            val matrix = Matrix().apply {
                postScale(scale, scale)
                postTranslate(x, yOffset)
            }
            canvas.drawBitmap(bitmap, matrix, paint)
            x += bitmap.width * scale
            
            if (shouldDrawSeparator(time, index)) {
                paint.colorFilter = whiteFilter
                x += drawSeparator(canvas, x, yOffset, bitmap.height * scale)
            } else if (index < time.lastIndex) {
                x += digitSpacing
            }
        }

        if (ampmStr.isNotEmpty()) {
            infoPaint.textSize = if (isLarge) ampmSize * 1.2f else ampmSize
            val ampmX = x + spacing
            val ampmY = timeY - (getCustomTextSize() * scale / 2f) + infoPaint.textSize - context.resources.getDimension(R.dimen.clock_oneplus_ampm_offset)
            canvas.drawText(ampmStr, ampmX, ampmY, infoPaint)
        }

        infoPaint.textSize = if (isLarge) weekSize * 1.5f else weekSize
        canvas.drawText(date, if (isLarge) width / 2f else startX, dateY, infoPaint)
    }
}
