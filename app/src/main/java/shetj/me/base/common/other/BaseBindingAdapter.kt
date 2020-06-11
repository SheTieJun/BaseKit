package shetj.me.base.common.other

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import me.shetj.base.ktx.loadImage

class BaseBindingAdapter {

    @BindingAdapter(value = ["android:onClick", "android:clickable"], requireAll = false)
    fun setOnClick(view: View, clickListener: View.OnClickListener?,
                   clickable: Boolean) {
        view.setOnClickListener(clickListener)
        view.isClickable = clickable
    }


    @BindingAdapter("android:onLayoutChange")
    fun setOnLayoutChangeListener(view: View, oldValue: View.OnLayoutChangeListener?,
                                  newValue: View.OnLayoutChangeListener?) {
        if (oldValue != null) {
            view.removeOnLayoutChangeListener(oldValue)
        }
        if (newValue != null) {
            view.addOnLayoutChangeListener(newValue)
        }
    }

    /**
     * <ImageView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:imageUrl="@{product.imageUrl}"/>
     */
    @BindingAdapter("imageUrl")
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
    fun setProgressBarColor(loader: ProgressBar?, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            loader?.indeterminateDrawable?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            loader?.indeterminateDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    @BindingAdapter(value = ["src", "placeholder", "error", "blur", "cropCircle"], requireAll = false)
    fun setGlideAdapter(view: ImageView?, src: String?, placeholder: Drawable?,
                        error: Drawable?, blurValue: Int, cropCircle: Boolean) {
        view?.let {

        }
    }
}