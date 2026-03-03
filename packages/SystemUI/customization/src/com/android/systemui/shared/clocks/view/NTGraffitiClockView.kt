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
import android.util.AttributeSet
import com.android.systemui.customization.R

/**
 * A street-art inspired clock view using Graffiti typography.
 */
class NTGraffitiClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FontBitmapClockView(context, attrs, defStyleAttr, defStyleRes) {

    override fun getTag(): String = if (isLarge) "NTGraffitiLargeClockView" else "NTGraffitiClockView"

    override fun getCustomTypeface(): Typeface = Typeface.create("Graffiti-Reg", Typeface.NORMAL)

    override fun getCustomTextSize(): Float = context.resources.getDimension(R.dimen.clock_staggered_text_size)

    override fun getCustomDigitYOffset(time: String, index: Int): Float {
        // Subtle bounce effect for digits
        return if (index % 2 == 0) -10f else 10f
    }
}
