/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.ktx

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.shape.MaterialShapeDrawable

/*** 获取背景颜色,also [com.google.android.material.shape.MaterialShapeUtils]
 * * [androidx.core.graphics.ColorUtils]
 */
fun Context.getMaterialShapeDrawable(
    @ColorRes colorInt: Int,
    elevation: Float
): MaterialShapeDrawable {
    val colorSurface = ContextCompat.getColor(this, colorInt)
    val materialShapeDrawable = MaterialShapeDrawable()
    materialShapeDrawable.initializeElevationOverlay(this)
    materialShapeDrawable.fillColor = ColorStateList.valueOf(colorSurface)
    materialShapeDrawable.elevation = elevation
    return materialShapeDrawable
}
