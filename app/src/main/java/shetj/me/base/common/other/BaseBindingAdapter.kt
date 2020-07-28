package shetj.me.base.common.other

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import me.shetj.base.ktx.loadImage

/**
 *     //A BindingAdapter in shetj.me.base.common.other.BaseBindingAdapter is not static and requires an object to use,
 *     需要  @JvmStatic
 */
object BaseBindingAdapter {


    /**
     * <ImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:imageUrl="@{product.imageUrl}"/>
     */
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageUrl(imageView: ImageView, url: String?) {
        if (url == null) {
            imageView.setImageDrawable(null)
        } else {
            imageView.loadImage(obj = url)
        }
    }

    /**
     *   bind:progressColor="@{@android:color/holo_green_dark}"
     */
    @Suppress("DEPRECATION")
    @BindingAdapter("progressColor")
    @JvmStatic
    fun setProgressBarColor(loader: ProgressBar?, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            loader?.indeterminateDrawable?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            loader?.indeterminateDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @JvmStatic
    @BindingAdapter(value = ["src", "placeholder", "error", "blur", "cropCircle"], requireAll = false)
    fun setGlideAdapter(view: ImageView?, src: String?, placeholder: Drawable?,
                        error: Drawable?, blurValue: Int, cropCircle: Boolean) {
        view?.let {

        }
    }
}