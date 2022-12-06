/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
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

package me.shetj.base.model

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.lifecycle.MutableLiveData


/**
 * Gray theme live data
 * 用来通知变成灰色主题
 * @constructor Create empty Gray theme live data
 */
class GrayThemeLiveData : MutableLiveData<Boolean>() {

    private val mPaint = Paint()
    private val mColorMatrix = ColorMatrix()

    fun getSatPaint(sat:Float = 1f): Paint {
        mColorMatrix.setSaturation(sat)
        mPaint.colorFilter = ColorMatrixColorFilter(mColorMatrix)
        return mPaint
    }

    companion object {

        @Volatile
        private var grayThemeLiveData: GrayThemeLiveData? = null

        @JvmStatic
        fun getInstance(): GrayThemeLiveData {
            return grayThemeLiveData ?: synchronized(GrayThemeLiveData::class.java) {
                return GrayThemeLiveData().apply {
                    grayThemeLiveData = this
                }
            }
        }
    }
}