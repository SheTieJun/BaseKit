package me.shetj.base.ktx

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.file.FileUtils
import me.shetj.base.tools.file.FileUtils.copyFile
import java.io.File

//region Glide 加载

@JvmOverloads
fun ImageView.loadImageBitmap(
    url: String? = null,
    @DrawableRes rId: Int? = null
) {
    Glide.with(context)
        .asBitmap()
        .load(url ?: rId)
        .into(this)
}

@JvmOverloads
fun ImageView.loadImageAny(
    url: String? = null,
    @DrawableRes rId: Int? = null,
    placeholderDrawable: Drawable? = null,
    errorDrawable: Drawable? = null
) {
    Glide.with(context)
        .load(url ?: rId)
        .placeholder(placeholderDrawable)
        .error(errorDrawable)
        .into(this)
}

@JvmOverloads
fun ImageView.loadImage(
    obj: Any,
    placeholderDrawable: Drawable? = null,
    errorDrawable: Drawable? = null
) {
    Glide.with(context)
        .load(obj)
        .placeholder(placeholderDrawable)
        .error(errorDrawable)
        .into(this)
}

/**
 * 使用Glide 下载图片
 */
inline fun downloadImage(context: Context, url: String, crossinline onSuccess: ((String) -> Unit)) {
    runOnIo {
        Glide.with(context).downloadOnly().load(url).submit().get().apply {
            onSuccess.invoke(this.absolutePath)
        }
    }
}

fun ImageView.loadImage(obj: Any, requestOptions: RequestOptions? = null) {
    Glide.with(context)
        .load(obj).apply {
            if (requestOptions != null) {
                apply(requestOptions)
            }
        }
        .into(this)
}

/**
 * 本地图片不使用缓存加载
 */
fun ImageView.loadImageNoCache(obj: Any) {
    Glide.with(context)
        .load(obj)
        .skipMemoryCache(true) // 不使用内存缓存
        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
        .into(this)
}

//endregion Glide 加载

//region 获取RequestOptions
@JvmOverloads
fun getRequestOptions(
    isCenterCrop: Boolean = true,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null,
    placeholderDrawable: Drawable? = null,
    errorDrawable: Drawable? = null,
    diskCacheStrategy: DiskCacheStrategy? = DiskCacheStrategy.AUTOMATIC
): RequestOptions {
    return RequestOptions().apply {
        if (isCenterCrop) {
            centerCrop()
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


fun AppCompatActivity.saveImage(shareCardUrl: String) {
    launch {
        val saveShareCard = saveImage(this@saveImage, shareCardUrl)
        saveShareCard.onSuccess {
            "图片保存成功：$it".showToast()
        }
        saveShareCard.onFailure {
            "图片保存失败".showToast()
        }
    }
}

/**
 * @author chunsheng
 * @date 2021/6/4
 * 保存分销邀请卡
 */
suspend fun saveImage(
    context: Context,
    shareCardUrl: String,
): Result<String> = withContext(Dispatchers.IO) {
    return@withContext kotlin.runCatching {
        val cacheFile = Glide.with(context)
            .downloadOnly()
            .load(shareCardUrl).submit().get()
        val filePath = (EnvironmentStorage.filesDir
                + "Image_" + System.currentTimeMillis() + ".jpg")
        val targetFile = File(filePath)
        val resultIsSuccess = copyFile(cacheFile, targetFile, FileUtils.OnReplaceListener {
            return@OnReplaceListener true
        })
        if (resultIsSuccess) {
            targetFile
        } else {
            cacheFile
        }.also {
            refreshAlbum(context, Uri.fromFile(it).toString())
        }.absolutePath

    }
}


