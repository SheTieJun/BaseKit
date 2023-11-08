

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

    fun getSatPaint(sat: Float = 1f): Paint {
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
