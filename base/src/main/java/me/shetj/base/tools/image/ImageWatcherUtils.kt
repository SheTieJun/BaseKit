package me.shetj.base.tools.image

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.SparseArray
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.github.ielse.imagewatcher.ImageWatcherHelper
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import kotlin.math.min

/**
 * 因为可能加载大图，所有自己多一层图片处理
 */
@Suppress("DEPRECATION")
class ImageWatcherUtils(activity: Activity) {

    private val iwHelper: ImageWatcherHelper
    private val maxSize = getMaxTextureSize()/2-1000
    private var mCompositeDisposable: CompositeDisposable? = null

    init {
        iwHelper = ImageWatcherHelper.with(activity) { context, uri, loadCallback ->
            Glide.with(context).load(uri)
                    //sim 加载原始大小图片
                    .into(object : com.bumptech.glide.request.target.SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            addDispose(Flowable.just(resource)
                                    .map {
                                        zoomDrawable(context,it)
                                    }.observeOn(AndroidSchedulers.mainThread())
                                    .subscribe( {
                                        loadCallback.onResourceReady(it)
                                    },{
                                        Timber.i("ImageWatcherUtils:${it.message}")
                                    }))
                        }
                    })
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap // drawable 转换成 bitmap
    {
        val width = drawable.intrinsicWidth // 取 drawable 的长宽
        val height = drawable.intrinsicHeight
        val config = if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565 // 取 drawable 的颜色格式
        val bitmap = Bitmap.createBitmap(width, height, config) // 建立对应 bitmap
        val canvas = Canvas(bitmap) // 建立对应 bitmap 的画布
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas) // 把 drawable 内容画到画布中
        return bitmap
    }

    fun zoomDrawable(context: Context, drawable: Drawable): Drawable? {

        return if (drawable.intrinsicHeight  >= maxSize || drawable.intrinsicWidth >= maxSize) {
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            val oldBmp = drawableToBitmap(drawable) // drawable 转换成 bitmap
            val matrix = Matrix() // 创建操作图片用的 Matrix 对象
            val scaleWidth =  maxSize.toFloat()/width // 计算缩放比例
            val scaleHeight = maxSize.toFloat()/height
            val min = min(scaleWidth, scaleHeight)
            matrix.postScale(min, min) // 设置缩放比例
            val newBmp = Bitmap.createBitmap(oldBmp, 0, 0, width, height, matrix, true) // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
            oldBmp.recycle()
            BitmapDrawable(context.resources, newBmp) // 把 bitmap 转换成 drawable 并返回
        }else{
            drawable
        }
    }

    /**
     * 将 [Disposable] 添加到 [CompositeDisposable] 中统一管理
     * 可在[android.app.Activity.onDestroy] 释放
     */
    fun addDispose(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 停止集合中正在执行的 RxJava 任务
     */
    fun unDispose() {
        mCompositeDisposable?.clear()
    }


    private fun getMaxTextureSize(): Int { // Safe minimum default size
        val IMAGE_MAX_BITMAP_DIMENSION = 2048
        // Get EGL Display
        val egl = EGLContext.getEGL() as EGL10
        val display: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        // Initialise
        val version = IntArray(2)
        egl.eglInitialize(display, version)
        // Query total number of configurations
        val totalConfigurations = IntArray(1)
        egl.eglGetConfigs(display, null, 0, totalConfigurations)
        // Query actual list configurations
        val configurationsList: Array<EGLConfig?> = arrayOfNulls(totalConfigurations[0])
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations)
        val textureSize = IntArray(1)
        var maximumTextureSize = 0
        // Iterate through all the configurations to located the maximum texture size
        for (i in 0 until totalConfigurations[0]) { // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize)
            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0]) maximumTextureSize = textureSize[0]
        }
        // Release
        egl.eglTerminate(display)
        // Return largest texture size found, or default
        return maximumTextureSize.coerceAtLeast(IMAGE_MAX_BITMAP_DIMENSION)
    }


    fun showPic(imageView: ImageView, url: String) {
        val dataList = ArrayList<String>()
        dataList.add(url)
        showPic(imageView, dataList, 0)
    }

    fun showPic(imageView: ImageView, dataList: List<String>, position: Int) {
        val mapping = SparseArray<ImageView>()
        mapping.put(position, imageView)
        showPic(imageView, dataList,mapping)
    }

    fun showPic(imageView: ImageView,dataList: List<String>,mapping: SparseArray<ImageView>) {
        showPic(imageView, mapping, convert(dataList))
    }

    fun showPic(imageView: ImageView, mapping: SparseArray<ImageView>,dataList: List<Uri>) {
        iwHelper.show(imageView, mapping, dataList)
    }


    private fun convert(data: List<String>): List<Uri> {
        val list = ArrayList<Uri>()
        for (datum in data) {
            list.add(Uri.parse(datum))
        }
        return list
    }

    fun onBackPressed(): Boolean {
        unDispose()
        return !iwHelper.handleBackPressed()
    }

}
