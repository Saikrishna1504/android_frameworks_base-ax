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

class NTStaggeredClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FontDigitClockView(context, attrs, defStyleAttr, defStyleRes) {

    override fun getTag(): String = if (isLarge) "NTStaggeredLargeClockView" else "NTStaggeredClockView"

    override fun getCustomTypeface(): Typeface = Typeface.create("Graffiti-Regular", Typeface.NORMAL)

    override fun getCustomTextSize(): Float = context.resources.getDimension(R.dimen.clock_staggered_text_size)

    private val offset get() = context.resources.getDimension(R.dimen.clock_staggered_offset)

    override fun getCustomDigitXOffset(time: String, index: Int): Float {
        return if (index < 2) -offset / 4f else offset / 4f
    }

    override fun getCustomDigitYOffset(time: String, index: Int): Float {
        return if (index < 2) -offset / 4f else offset / 2f
    }
}
