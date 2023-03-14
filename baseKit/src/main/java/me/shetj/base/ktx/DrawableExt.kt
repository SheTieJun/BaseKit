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
