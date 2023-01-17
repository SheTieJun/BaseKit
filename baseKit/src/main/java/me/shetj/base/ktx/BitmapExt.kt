
package me.shetj.base.ktx

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null else if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    val intrinsicWidth = drawable.intrinsicWidth
    val intrinsicHeight = drawable.intrinsicHeight
    return if (!(intrinsicWidth > 0 && intrinsicHeight > 0)) null else try {
        val config = if (drawable.opacity != PixelFormat.OPAQUE) ARGB_8888 else RGB_565
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        null
    }
}