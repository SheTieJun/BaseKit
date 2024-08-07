package me.shetj.base.tools.app

import android.app.Activity
import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.AttrRes
import androidx.annotation.Keep
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.util.TypedValueCompat
import androidx.core.view.WindowCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.shetj.base.BaseKit
import me.shetj.base.ktx.logW
import me.shetj.base.ktx.setAppearance
import me.shetj.base.ktx.setClicksAnimate
import me.shetj.base.ktx.setSwipeRefresh
import me.shetj.base.ktx.toMessage
import me.shetj.base.tools.file.EnvironmentStorage
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest

/**
 * ================================================
 * 一些框架常用的工具
 */
@Keep
class ArmsUtils private constructor() {

    init {
        throw IllegalStateException("you can't instantiate me!")
    }

    companion object {
        var mToast: Toast? = null

        /**
         * 为 View 添加点击态
         * @param view
         */
        @JvmStatic
        fun addScaleTouchEffect(vararg view: View) {
            for (v in view) {
                v.setClicksAnimate()
            }
        }

        /**
         * 获得资源
         */
        @JvmStatic
        fun getResources(context: Context): Resources {
            return context.resources
        }

        /**
         * 得到字符数组
         */
        @JvmStatic
        fun getStringArray(context: Context, id: Int): Array<String> {
            return getResources(context).getStringArray(id)
        }

        /**
         * 从 dimens 中获得尺寸
         * @param context
         * @param id
         * @return
         */
        @JvmStatic
        fun getDimens(context: Context, id: Int): Int {
            return getResources(context).getDimension(id).toInt()
        }

        /**
         * 从 dimens 中获得尺寸
         *
         * @param context
         * @param dimenName
         * @return
         */
        @JvmStatic
        fun getDimens(context: Context, dimenName: String): Float {
            return getResources(context).getDimension(
                getResources(context).getIdentifier(
                    dimenName,
                    "dimen",
                    context.packageName
                )
            )
        }

        /**
         * 从String 中获得字符
         *
         * @return
         */
        @JvmStatic
        fun getString(context: Context, stringID: Int): String {
            return getResources(context).getString(stringID)
        }

        /**
         * 从String 中获得字符
         *
         * @return
         */
        @JvmStatic
        fun getString(context: Context, strName: String): String {
            return getString(
                context,
                getResources(context).getIdentifier(strName, "string", context.packageName)
            )
        }

        /**
         * findview
         *
         * @param view
         * @param viewName
         * @param <T>
         * @return
         </T> */
        @JvmStatic
        fun <T : View> findViewByName(context: Context, view: View, viewName: String): T {
            val id = getResources(context).getIdentifier(viewName, "id", context.packageName)
            return view.findViewById(id)
        }

        /**
         * findview
         *
         * @param activity
         * @param viewName
         * @param <T>
         * @return
         </T> */
        @JvmStatic
        fun <T : View> findViewByName(context: Context, activity: Activity, viewName: String): T {
            val id = getResources(context).getIdentifier(viewName, "id", context.packageName)
            return activity.findViewById(id)
        }

        /**
         * 根据 layout 名字获得 id
         *
         * @param layoutName
         * @return
         */
        @JvmStatic
        fun findLayout(context: Context, layoutName: String): Int {
            return getResources(context).getIdentifier(layoutName, "layout", context.packageName)
        }

        /**
         * 填充view
         *
         * @param detailScreen
         * @return
         */
        @JvmStatic
        fun inflate(context: Context, detailScreen: Int): View {
            return View.inflate(context, detailScreen, null)
        }

        /**
         * 单例 toast
         * @param string
         */
        @JvmStatic
        @MainThread
        fun makeText(string: String) {
            if (mToast == null) {
                mToast = Toast.makeText(Utils.app, string, Toast.LENGTH_SHORT)
            }
            mToast?.setText(string)
            mToast?.show()
        }

        /**
         * 跳转界面 3
         *
         * @param activity
         * @param homeActivityClass
         */
        @JvmStatic
        fun startActivity(activity: Activity, homeActivityClass: Class<*>) {
            val intent = Intent(activity.applicationContext, homeActivityClass)
            startActivity(activity, intent)
        }

        /**
         * 跳转界面 4
         *
         * @param
         */
        @JvmStatic
        fun startActivity(activity: Activity, intent: Intent, userTransition: Boolean = false) {
            if (userTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
                )
            } else {
                activity.startActivity(intent)
            }
        }

        @JvmStatic
        fun getScreenWidth(): Int {
            return getScreenWidth(Utils.app.applicationContext)
        }

        /**
         * 获得屏幕的高度
         *
         * @return
         */
        @JvmStatic
        fun getScreenHeight(): Int {
            return getScreenHeight(Utils.app.applicationContext)
        }

        /**
         * 获得屏幕的宽度
         *
         * @return
         */
        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            return getResources(context).displayMetrics.widthPixels
        }

        /**
         * 获得屏幕的高度
         *
         * @return
         */
        @JvmStatic
        fun getScreenHeight(context: Context): Int {
            return getResources(context).displayMetrics.heightPixels
        }

        /**
         * 获得颜色
         */
        @JvmStatic
        fun getColor(context: Context, rid: Int): Int {
            return ContextCompat.getColor(context, rid)
        }

        fun resolve(context: Context, @AttrRes attributeResId: Int): TypedValue? {
            val typedValue = TypedValue()
            return if (context.theme.resolveAttribute(attributeResId, typedValue, true)) {
                typedValue
            } else {
                null
            }
        }

        /**
         * 移除孩子
         *
         * @param view
         */
        @JvmStatic
        fun removeChild(view: View) {
            val parent = view.parent
            if (parent is ViewGroup) {
                parent.removeView(view)
            }
        }

        @Suppress("UNCHECKED_CAST", "CyclomaticComplexMethod")
        @JvmStatic
        fun saveStateToBundle(saveStateMap: MutableMap<String, Any>, outState: Bundle) {
            saveStateMap.forEach {
                val key = it.key
                when (val value = it.value) {
                    is String -> outState.putString(key, value)
                    is Int -> outState.putInt(key, value)
                    is Boolean -> outState.putBoolean(key, value)
                    is Float -> outState.putFloat(key, value)
                    is Long -> outState.putLong(key, value)
                    is Double -> outState.putDouble(key, value)
                    is Short -> outState.putShort(key, value)
                    is Byte -> outState.putByte(key, value)
                    is Char -> outState.putChar(key, value)
                    is CharSequence -> outState.putCharSequence(key, value)
                    is Bundle -> outState.putBundle(key, value)
                    is ArrayList<*> -> {
                        when {
                            value.isEmpty() -> {}
                            value[0] is Int -> {
                                outState.putIntegerArrayList(key, value as ArrayList<Int>)
                            }

                            value[0] is String -> {
                                outState.putStringArrayList(key, value as ArrayList<String>)
                            }

                            value[0] is CharSequence -> {
                                outState.putCharSequenceArrayList(key, value as ArrayList<CharSequence>)
                            }

                            value[0] is Parcelable -> {
                                outState.putParcelableArrayList(key, value as ArrayList<Parcelable>)
                            }

                            else -> "Unsupported bundle component (${value.javaClass})".logW()
                        }
                    }

                    is Array<*> -> {
                        when {
                            value.isEmpty() -> {}
                            value.isArrayOf<CharSequence>() -> outState.putCharSequenceArray(
                                key,
                                value as Array<out CharSequence>
                            )

                            value.isArrayOf<String>() -> outState.putStringArray(key, value as Array<out String>)
                            value.isArrayOf<Parcelable>() -> outState.putParcelableArray(
                                key,
                                value as Array<out Parcelable>
                            )

                            else -> "Unsupported bundle component (${value.javaClass})".logW()
                        }
                    }

                    is Parcelable -> outState.putParcelable(key, value)
                    is java.io.Serializable -> outState.putSerializable(key, value)
                    else -> "Unsupported bundle component (${value.javaClass})".logW()
                }
            }
        }

        /**
         * MD5
         *
         * @param string
         * @return
         * @throws Exception
         */
        @JvmStatic
        fun encodeToMD5(string: String): String {
            var hash = ByteArray(0)
            try {
                hash = MessageDigest.getInstance("MD5").digest(
                    string.toByteArray(charset("UTF-8"))
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val hex = StringBuilder(hash.size * 2)
            for (b in hash) {
                if (b.toInt() and 0xFF < 0x10) {
                    hex.append("0")
                }
                hex.append(Integer.toHexString((b.toInt() and 0xFF)))
            }
            return hex.toString()
        }

        /**
         * 全屏,并且
         * @param activity
         * @param isBlack 是否是黑色的字体和icon
         */
        @JvmStatic
        @JvmOverloads
        fun statuInScreen2(activity: ComponentActivity, isBlack: Boolean = false) {
            activity.statuInScreen(isBlack)
        }

        /**
         * 沉侵式状态栏
         */
        @JvmStatic
        @JvmOverloads
        fun ComponentActivity.statuInScreen(isBlack: Boolean = false) {
            // 关键代码
            WindowCompat.setDecorFitsSystemWindows(window, false)
            setAppearance(isBlack)
        }

        @JvmStatic
        fun setSwipeRefresh(
            mSwipeRefreshLayout: SwipeRefreshLayout,
            them2Color: Int,
            listener: SwipeRefreshLayout.OnRefreshListener
        ) {
            mSwipeRefreshLayout.setSwipeRefresh(them2Color, listener)
        }

        @Throws(IOException::class)
        @JvmStatic
        fun getAssetsFile(fileName: String): InputStream {
            return Utils.app.applicationContext.assets.open(fileName)
        }

        private var density = -1f

        private fun getDensity(): Float {
            if (density <= 0f) {
                density = BaseKit.app.resources.displayMetrics.density
            }
            return density
        }

        @JvmStatic
        fun dp2px(dpValue: Float): Int {
            return TypedValueCompat.dpToPx(dpValue, BaseKit.app.resources.displayMetrics).toInt()
        }

        @JvmStatic
        fun px2dp(pxValue: Float): Int {
            return TypedValueCompat.pxToDp(pxValue, BaseKit.app.resources.displayMetrics).toInt()
        }

        @JvmStatic
        @NonNull
        fun getMessage(code: Int, obj: Any): Message {
            return obj.toMessage(code)
        }

        @JvmStatic
        fun getActivityHeight(context: Context?): Int {
            if (null == context) {
                return getScreenHeight()
            }
            val outRect = Rect()
            try {
                (context as Activity).window.decorView.getWindowVisibleDisplayFrame(outRect)
            } catch (e: ClassCastException) {
                e.printStackTrace()
                return getScreenHeight(context)
            }
            return outRect.height()
        }

        /**
         * 为app创建保存路径，
         * 如果不存在需要makdir
         */
        @JvmStatic
        fun getAppPath(): String {
            val sb = StringBuilder()
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                sb.append(EnvironmentStorage.sdCardPath)
            } else {
                sb.append(Environment.getDataDirectory().path)
            }
            sb.append(File.separator)
            sb.append(AppUtils.appPackageName)
            sb.append(File.separator)
            return sb.toString()
        }

        fun copyText(context: Context, text: String) {
            val cm: ClipboardManager? =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            // 创建普通字符型ClipData
            val mClipData = ClipData.newPlainText("Label", text)
            // 将ClipData内容放到系统剪贴板里。
            cm?.setPrimaryClip(mClipData)
        }

        /**
         * 获取粘贴的文字
         * Android 10 开始需要延迟去获取
         */
        fun paste(context: Context): String? {
            val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            if (manager != null) {
                if (manager.hasPrimaryClip() && (manager.primaryClip?.itemCount ?: 0) > 0) {
                    val addedText = manager.primaryClip?.getItemAt(0)?.text
                    return addedText.toString()
                }
            }
            return null
        }
    }
}
