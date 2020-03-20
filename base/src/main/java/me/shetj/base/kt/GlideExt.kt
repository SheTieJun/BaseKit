package me.shetj.base.kt

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
//region Glide 加载

@JvmOverloads
fun ImageView.loadImageBitmap(url :String ?=null,
                              isCenterCrop:Boolean = true,
                              @DrawableRes rId :Int? = null,
                              @DrawableRes placeholder :Int? = null){
    val requestOptions = getRequestOptions(isCenterCrop, placeholder)
    Glide.with(context)
            .asBitmap()
            .load(url?:rId)
            .apply(requestOptions)
            .into(this)
}

@JvmOverloads
fun ImageView.loadImage(url :String ?=null,
                        isCenterCrop:Boolean = true,
                        @DrawableRes rId :Int? = null,
                        @DrawableRes placeholder :Int? = null,
                        placeholderDrawable: Drawable ?= null,
                        errorDrawable: Drawable? =null){
    val requestOptions = getRequestOptions(isCenterCrop, placeholder,
            placeholderDrawable = placeholderDrawable,
            errorDrawable = errorDrawable)
    Glide.with(context)
            .load(url?:rId)
            .apply(requestOptions)
            .into(this)
}
//endregion Glide 加载

@JvmOverloads
fun getRequestOptions(isCenterCrop: Boolean = true,
                      @DrawableRes placeholder: Int? = null,
                      @DrawableRes error :Int? = null,
                      placeholderDrawable: Drawable ?= null,
                      errorDrawable: Drawable? =null
): RequestOptions {
    return RequestOptions().apply {
        if (isCenterCrop) {
            centerCrop()
        } else {
            fitCenter()
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