package me.shetj.base.tools.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.annotations.NonNull
import me.shetj.base.s
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest


/**
 * ================================================
 * 一些框架常用的工具
 *
 *
 *
 * @author JessYan
 * @date 2015/11/23
 * @update update by shetj 2018年4月11日10:15:40
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
@Keep
class ArmsUtils private constructor() {


    init {
        throw IllegalStateException("you can't instantiate me!")
    }

    companion object {
        var mToast: Toast? = null

        /**
         * 设置hint大小
         *
         * @param size
         * @param v
         * @param res
         */
        fun setViewHintSize(context: Context, size: Int, v: TextView, res: Int) {
            val ss = SpannableString(getResources(context).getString(
                    res))
            // 新建一个属性对象,设置文字的大小
            val ass = AbsoluteSizeSpan(size, true)
            // 附加属性到文本
            ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // 设置hint
            v.hint = SpannedString(ss)
        }

        /**
         * 全面屏幕检查
         * @param activity
         */
        fun checkIsNotchScreen(activity: Activity): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val windowInsets = activity.window.decorView.rootWindowInsets
                if (windowInsets != null) {
                    val displayCutout = windowInsets.displayCutout
                    if (displayCutout != null) {
                        val rects = displayCutout.boundingRects
                        //通过判断是否存在rects来确定是否刘海屏手机
                        return rects != null && rects.size > 0
                    }
                }
            }
            return false
        }

        /**
         * 设置命名常亮
         * other :android:keepScreenOn="true"
         * @param activity 常亮的界面
         */
        fun wakey(activity: Activity) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        /**
         * 获得资源
         */
        fun getResources(context: Context): Resources {
            return context.resources
        }

        /**
         * 得到字符数组
         */
        fun getStringArray(context: Context, id: Int): Array<String> {
            return getResources(context).getStringArray(id)
        }


        /**
         * 从 dimens 中获得尺寸
         *
         * @param context
         * @param id
         * @return
         */
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
        fun getDimens(context: Context, dimenName: String): Float {
            return getResources(context).getDimension(getResources(context).getIdentifier(dimenName, "dimen", context.packageName))
        }

        /**
         * 从String 中获得字符
         *
         * @return
         */

        fun getString(context: Context, stringID: Int): String {
            return getResources(context).getString(stringID)
        }

        /**
         * 从String 中获得字符
         *
         * @return
         */

        fun getString(context: Context, strName: String): String {
            return getString(context, getResources(context).getIdentifier(strName, "string", context.packageName))
        }

        /**
         * findview
         *
         * @param view
         * @param viewName
         * @param <T>
         * @return
        </T> */
        fun <T : View> findViewByName(context: Context, view: View, viewName: String): T {
            val id = getResources(context).getIdentifier(viewName, "id", context.packageName)
            val v = view.findViewById<T>(id)
            return v
        }

        /**
         * findview
         *
         * @param activity
         * @param viewName
         * @param <T>
         * @return
        </T> */
        fun <T : View> findViewByName(context: Context, activity: Activity, viewName: String): T {
            val id = getResources(context).getIdentifier(viewName, "id", context.packageName)
            val v = activity.findViewById<T>(id)
            return v
        }

        /**
         * 根据 layout 名字获得 id
         *
         * @param layoutName
         * @return
         */
        fun findLayout(context: Context, layoutName: String): Int {
            val id = getResources(context).getIdentifier(layoutName, "layout", context.packageName)
            return id
        }

        /**
         * 填充view
         *
         * @param detailScreen
         * @return
         */
        fun inflate(context: Context, detailScreen: Int): View {
            return View.inflate(context, detailScreen, null)
        }

        /**
         * 单例 toast
         *
         * @param string
         */
        fun makeText(string: String) {
            if (mToast == null) {
                mToast = Toast.makeText(Utils.app.applicationContext, string, Toast.LENGTH_SHORT)
            }
            mToast!!.setText(string)
            mToast!!.show()
        }

        /**
         * 使用 [Snackbar] 显示文本消息
         *
         */
        fun shortSnackbar(activity: Activity, message: String) {
            val view = activity.window.decorView.findViewById<View>(android.R.id.content)
            SnackbarUtil.ShortSnackbar(view, message, SnackbarUtil.Info).show()
        }

        /**
         * 使用 [Snackbar] 长时间显示文本消息
         */
        fun longSnackbar(activity: Activity, message: String) {
            val view = activity.window.decorView.findViewById<View>(android.R.id.content)
            SnackbarUtil.LongSnackbar(view, message, SnackbarUtil.Warning).show()
        }


        /**
         * 通过资源id获得drawable
         *
         * @param rID
         * @return
         */
        fun getDrawablebyResource(context: Context, rID: Int): Drawable? {
            return ContextCompat.getDrawable(context, rID)
        }

        /**
         * 跳转界面 3
         *
         * @param activity
         * @param homeActivityClass
         */
        fun startActivity(activity: Activity, homeActivityClass: Class<*>) {
            val intent = Intent(activity.applicationContext, homeActivityClass)
            activity.startActivity(intent)
        }

        /**
         * 跳转界面 4
         *
         * @param
         */
        fun startActivity(activity: Activity, intent: Intent) {
            activity.startActivity(intent)
        }

        /**
         * 获得屏幕的宽度
         *
         * @return
         */
        fun getScreenWidth(context: Context): Int {
            return getResources(context).displayMetrics.widthPixels
        }

        /**
         * 获得屏幕的高度
         *
         * @return
         */
        fun getScreenHeight(context: Context): Int {
            return getResources(context).displayMetrics.heightPixels
        }


        /**
         * 获得颜色
         */
        fun getColor(context: Context, rid: Int): Int {
            return ContextCompat.getColor(context, rid)
        }

        /**
         * 获得颜色
         */
        fun getColor(context: Context, colorName: String): Int {
            return getColor(context, ResourceUtils.getIdByName(context, colorName, "color"))
        }

        /**
         * 移除孩子
         *
         * @param view
         */
        fun removeChild(view: View) {
            val parent = view.parent
            if (parent is ViewGroup) {
                parent.removeView(view)
            }
        }


        fun getRxPermissions(activity: FragmentActivity): RxPermissions {
            return RxPermissions(activity)
        }

        /**
         * MD5
         *
         * @param string
         * @return
         * @throws Exception
         */
        fun encodeToMD5(string: String): String {
            var hash = ByteArray(0)
            try {
                hash = MessageDigest.getInstance("MD5").digest(
                        string.toByteArray(charset("UTF-8")))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val hex = StringBuilder(hash.size * 2)
            for (b in hash) {
                if (b.toInt() and 0xFF< 0x10) {
                        hex.append("0")
                }
                hex.append(Integer.toHexString((b.toInt() and 0xFF)))
            }
            return hex.toString()
        }

        /**
         * 设置透明状态栏与导航栏
         *
         * @param navi true不设置导航栏|false设置导航栏
         */
        fun setStatusBar(activity: Activity, navi: Boolean) {
            //api>21,全透明状态栏和导航栏;api>19,半透明状态栏和导航栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
                if (navi) {
                    //状态栏不会被隐藏但activity布局会扩展到状态栏所在位置
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//导航栏不会被隐藏但activity布局会扩展到导航栏所在位置

                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_FULLSCREEN)
                    window.navigationBarColor = Color.TRANSPARENT
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (navi) {
                    //半透明导航栏
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }
                //半透明状态栏
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }


        /**
         * 全屏,并且沉侵式状态栏
         *
         * @param activity
         */
        fun statuInScreen(activity: Activity, isBlack: Boolean) {
            // 沉浸式状态栏
            QMUIStatusBarHelper.translucent(activity)
            if (isBlack) {
                QMUIStatusBarHelper.setStatusBarLightMode(activity)
            } else {
                QMUIStatusBarHelper.setStatusBarDarkMode(activity)
            }
        }

        /**
         * 全屏
         */
        fun fullScreencall(activity: Activity) {
            if (Build.VERSION.SDK_INT < 19) {
                val v = activity.window.decorView
                v.systemUiVisibility = View.GONE
            } else if (Build.VERSION.SDK_INT >= 19) {
                val decorView = activity.window.decorView
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                if (Build.VERSION.SDK_INT >= 21) {
                    activity.window.statusBarColor = Color.TRANSPARENT
                    activity.window.navigationBarColor = Color.TRANSPARENT
                }
            }
        }

        /**
         * 配置 recycleview
         *
         * @param recyclerView
         * @param layoutManager
         */
        fun configRecycleView(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager) {
            recyclerView.layoutManager = layoutManager
            //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = DefaultItemAnimator()
        }


        fun convertStatusCode(code :Int): String {
            val msg: String
            if (code == 500) {
                msg = "服务器发生错误"
            } else if (code == 404) {
                msg = "请求地址不存在"
            } else if (code == 403) {
                msg = "请求被服务器拒绝"
            } else if (code == 307) {
                msg = "请求被重定向到其他页面"
            } else {
                msg = "其他错误"
            }
            return msg
        }

        fun setSwipeRefresh(mSwipeRefreshLayout: SwipeRefreshLayout,
                            them2Color: Int, listener: SwipeRefreshLayout.OnRefreshListener) {
            mSwipeRefreshLayout.setColorSchemeResources(them2Color)
            mSwipeRefreshLayout.setOnRefreshListener(listener)
        }

        /**
         * @param root 最外层布局，需要调整的布局
         * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
         */
        fun controlKeyboardLayout(root: View, scrollToView: View) {
            root.viewTreeObserver.addOnGlobalLayoutListener {
                val rect = Rect()
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect)
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                val rootInvisibleHeight = root.rootView.height - rect.bottom
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    val location = IntArray(2)
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location)
                    //计算root滚动高度，使scrollToView在可见区域的底部
                    val scrollHeight = location[1] + scrollToView.height - rect.bottom
                    root.scrollTo(0, scrollHeight)
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0)
                }
            }
        }

        @Throws(IOException::class)
        fun getAssetsFile(fileName: String): InputStream {
            return Utils.app.applicationContext.assets.open(fileName)
        }


        private var density = -1f

        fun getDensity(): Float {
            if (density <= 0f) {
                density = s.app.resources.displayMetrics.density
            }
            return density
        }

        fun dip2px(dpValue: Float): Int {
            return (dpValue * getDensity() + 0.5f).toInt()
        }

        fun px2dip(pxValue: Float): Int {
            return (pxValue / getDensity() + 0.5f).toInt()
        }
    }

    @NonNull
     fun getMessage(code: Int, obj: Any): Message {
        return Message.obtain().getMessage(code,obj)
    }
}