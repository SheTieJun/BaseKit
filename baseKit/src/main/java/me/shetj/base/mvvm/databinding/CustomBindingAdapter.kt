@file:Suppress("IgnoredReturnValue")

package me.shetj.base.mvvm.databinding

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import me.shetj.base.R

/**
 * Custom Binding Adapter : which used in data binding for managing views and model value,
 * here all custom binding functions are added.
 */

/**
 * to load url based image assign base url of image.
 */
const val IMAGE_BASE_URL = ""

/**
 * method which manages the visibility of views
 *
 * @param view the view which going to used for visibility.
 * @param isVisible passed from viewVisibility which make view visible or gone.
 * @param isInvisible passed from isInvisible which make view invisible once viewVisibility is false.
 */
@BindingAdapter(value = ["viewVisibility", "isInvisible"], requireAll = false)
fun setVisibility(view: View, isVisible: Boolean, isInvisible: Boolean) {
    view.visibility =
        if (isVisible) View.VISIBLE else if (isInvisible) View.INVISIBLE else View.GONE
}

/**
 * method which loads images into imageView
 *
 * @param imageView the image view which used to present image.
 * @param url the string path to load image from file/internet/resource path.
 * @param placeHolder the Drawable used when image which used to present default image before image loads.
 * @param placeHolderError the Drawable used when found any error to loading the image we are going to uses passed image.
 * @param cornerRadius Float type value which used to curve the rectangle as much as passed value.
 */
@BindingAdapter(
    value = ["imageUrl", "placeHolder", "placeHolderError", "cornerRadius", "circular"],
    requireAll = false
)
fun loadImageFromNetwork(
    imageView: ImageView,
    url: String?,
    placeHolder: Drawable?,
    placeHolderError: Drawable?,
    cornerRadius: Float,
    circular: Boolean
) {
    var imageURL = url

    if (imageURL != null && imageURL.isNotEmpty() && !imageURL.equals(
            "",
            ignoreCase = true
        )
    ) {
        imageURL = imageURL.trim { it <= ' ' }
        if (imageURL.startsWith("/")) {
            imageURL = IMAGE_BASE_URL + url
        }

        val options = RequestOptions()
            .placeholder(placeHolder)
            .error(placeHolderError)

        if (circular) {
            options.circleCrop()
        }

        if (cornerRadius > 0) {
            options.transform(CenterCrop(), RoundedCorners(cornerRadius.toInt()))
        }

        Glide.with(imageView).load(imageURL).apply(options).into(imageView)
    } else {
        imageView.setImageDrawable(placeHolder)
    }
}

/**
 * method which loads images into imageView
 *
 * @param imageView the image view which used to present image.
 * @param imageRes the Drawable used to render the image.
 * @param cornerRadius Float type value which used to curve the rectangle as much as passed value.
 * @param circular the Boolean which used to identify the image is circle or not.
 */
@BindingAdapter(
    value = ["imageRes", "cornerRadius", "circular"],
    requireAll = false
)
fun loadImageFromResource(
    imageView: ImageView,
    imageRes: Drawable?,
    cornerRadius: Float,
    circular: Boolean
) {
    if (imageRes == null) {
        imageView.setImageResource(R.drawable.image_not_found)
    } else {
        val options = RequestOptions()
        if (circular) {
            options.circleCrop()
        }
        if (cornerRadius > 0) {
            options.transform(CenterCrop(), RoundedCorners(cornerRadius.toInt()))
        }
        Glide.with(imageView).load(imageRes).apply(options).into(imageView)
    }
}

/**
 * method which adds spacing into recycler view
 *
 * @param recyclerView the recycler list view
 * @param itemSpace the double value to providing space between items
 */
@BindingAdapter(value = ["itemSpace", "includeEdge"], requireAll = false)
fun addSpaceBetweenRecyclerItem(
    recyclerView: RecyclerView,
    itemSpace: Double,
    includeEdge: Boolean
) {
    recyclerView.addItemDecoration(
        RecyclerItemDecoration(itemSpace.toInt(), includeEdge)
    )
}

/**
 * method which set underline into TextView
 * @param textView the Text view
 * @param isTextUnderline passed from textUnderline which set underline or not.
 */
@BindingAdapter(value = ["textUnderline"], requireAll = false)
fun setTextUnderline(
    textView: TextView,
    isTextUnderline: Boolean
) {
    if (isTextUnderline) {
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}

/*数据变化，刷新 UI*/
@BindingAdapter("android:refreshing", requireAll = false)
fun setSwipeRefreshing(view: SwipeRefreshLayout, oldValue: Boolean, newValue: Boolean) {
    // 判断是否是新的值，避免陷入死循环
    if (oldValue != newValue) {
        view.isRefreshing = newValue
    }
}

/*ui 的状态，反向绑定给数据变化*/
@BindingAdapter("android:refreshingAttrChanged", requireAll = false)
fun setRefreshCallback(view: SwipeRefreshLayout, listener: InverseBindingListener?) {
    listener ?: return
    view.setOnRefreshListener {
        // 由ui层的刷新状态变化，反向通知数据层的变化
        listener.onChange()
    }
}

/* 反向绑定的实现,将UI的变化，回调给bindingListener，listener就会onChange，通知数据变化*/
@InverseBindingAdapter(attribute = "android:refreshing", event = "android:refreshingAttrChanged")
fun isSwipeRefreshing(view: SwipeRefreshLayout): Boolean {
    return view.isRefreshing
}
