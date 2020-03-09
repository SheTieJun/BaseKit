package me.shetj.base.kt

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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
              @DrawableRes placeholder :Int? = null){
    val requestOptions = getRequestOptions(isCenterCrop, placeholder)
    Glide.with(context)
            .load(url?:rId)
            .apply(requestOptions)
            .into(this)
}


@JvmOverloads
fun getRequestOptions(isCenterCrop: Boolean = true, @DrawableRes placeholder: Int? = null): RequestOptions {
    return RequestOptions().apply {
        if (isCenterCrop) {
            centerCrop()
        } else {
            fitCenter()
        }
        placeholder?.let {
            placeholder(placeholder)
            error(placeholder)
        }
    }
}