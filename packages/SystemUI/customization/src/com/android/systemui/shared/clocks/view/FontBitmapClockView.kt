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

/**
 * A base class for font-based clocks that pre-renders digits into bitmaps.
 */
abstract class FontBitmapClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : BitmapDigitClockView(context, attrs, defStyleAttr, defStyleRes) {

    protected var isLarge: Boolean = false

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NTClockView)
        isLarge = ta.getBoolean(R.styleable.NTClockView_isLarge, false)
        ta.recycle()
    }

    abstract fun getCustomTypeface(): Typeface
    abstract fun getCustomTextSize(): Float

    override fun createDigitBitmaps(): Map<Char, Bitmap?> {
        val typeface = getCustomTypeface()
        val textSize = getCustomTextSize()
        
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = textSize
            this.color = Color.WHITE
        }

        val bitmaps = mutableMapOf<Char, Bitmap?>()
        val bounds = Rect()

        for (char in '0'..'9') {
            val charStr = char.toString()
            textPaint.getTextBounds(charStr, 0, 1, bounds)
            
            val width = textPaint.measureText(charStr).toInt().coerceAtLeast(1)
            val height = (textPaint.fontMetrics.bottom - textPaint.fontMetrics.top).toInt().coerceAtLeast(1)
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            canvas.drawText(charStr, 0f, -textPaint.fontMetrics.top, textPaint)
            bitmaps[char] = bitmap
        }
        
        return bitmaps
    }

    override fun clockColor(): Int = clockColor
}
