package me.shetj.base.kt

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

//region Glide 加载

@JvmOverloads
fun ImageView.loadImageBitmap(url: String? = null,
                              isCenterCrop: Boolean = true,
                              @DrawableRes rId: Int? = null,
                              @DrawableRes placeholder: Int? = null) {
    val requestOptions = getRequestOptions(isCenterCrop, placeholder)
    Glide.with(context)
            .asBitmap()
            .load(url ?: rId)
            .apply(requestOptions)
            .into(this)
}

@JvmOverloads
fun ImageView.loadImage(url: String? = null,
                        isCenterCrop: Boolean = true,
                        @DrawableRes rId: Int? = null,
                        @DrawableRes placeholder: Int? = null,
                        placeholderDrawable: Drawable? = null,
                        errorDrawable: Drawable? = null) {
    val requestOptions = getRequestOptions(isCenterCrop, placeholder,
            placeholderDrawable = placeholderDrawable,
            errorDrawable = errorDrawable)
    url ?: rId?.let { loadByGlide(it, requestOptions) }
}

@JvmOverloads
fun ImageView.loadImage(obj: Any,
                        isCenterCrop: Boolean = true,
                        @DrawableRes placeholder: Int? = null,
                        placeholderDrawable: Drawable? = null,
                        errorDrawable: Drawable? = null) {
    val requestOptions = getRequestOptions(isCenterCrop, placeholder,
            placeholderDrawable = placeholderDrawable,
            errorDrawable = errorDrawable)
    loadByGlide(obj, requestOptions)
}

/**
 * 使用Glide 下载图片
 */
fun downloadImage(context: Context, url: String, onSuccess: ((String) -> Unit)? = null) {
    Glide.with(context).downloadOnly().load(url).submit().get().apply {
        onSuccess?.invoke(this.absolutePath)
    }
}


internal fun ImageView.loadByGlide(obj: Any, requestOptions: RequestOptions) {
    Glide.with(context)
            .load(obj)
            .apply(requestOptions)
            .into(this)
}
//endregion Glide 加载

//region 获取RequestOptions
@JvmOverloads
fun getRequestOptions(isCenterCrop: Boolean = true,
                      @DrawableRes placeholder: Int? = null,
                      @DrawableRes error: Int? = null,
                      placeholderDrawable: Drawable? = null,
                      errorDrawable: Drawable? = null,
                      diskCacheStrategy: DiskCacheStrategy? = DiskCacheStrategy.AUTOMATIC
): RequestOptions {
    return RequestOptions().apply {
        if (isCenterCrop) {
            centerCrop()
        } else {
            fitCenter()
        }
        diskCacheStrategy?.let {
            diskCacheStrategy(diskCacheStrategy)
        }
        placeholder?.let {
            placeholder(it)
        }
        error?.let {
            error(it)
        }
        placeholderDrawable?.let {
            placeholder(it)
        }
        errorDrawable?.let {
            error(it)
        }
    }
}
//endregion

//region private method
private fun checkIsImage(obj: Any): Boolean {
    return obj is String || obj is Int
}
//endregion
