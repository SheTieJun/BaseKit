package me.shetj.base.ktx

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Looper
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import io.reactivex.rxjava3.schedulers.Schedulers
import me.shetj.base.base.TaskExecutor
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.app.SoftKeyBoardListener
import java.lang.reflect.Method


/**
 * 用来防止重新进入的时候多次展示 Splash
 * 是否是栈的底部
 */
fun AppCompatActivity.isRoot() {
    if (!isTaskRoot) {
        finish()
    }
}

@JvmOverloads
inline fun <reified T:Activity> Context.start(isFinish: Boolean = false) {
    ArmsUtils.startActivity(this as AppCompatActivity, T::class.java)
    if (isFinish) {
        finish()
    }
}

@JvmOverloads
fun Context.start(intent: Intent, isFinish: Boolean = false) {
    ArmsUtils.startActivity(this as AppCompatActivity, intent)
    if (isFinish) {
        finish()
    }
}

/**
 * @param isFinishOnTouchOutside 是否点击window 关闭activity
 */
@JvmOverloads
fun AppCompatActivity.cleanBackground(isFinishOnTouchOutside: Boolean = true) {
    val mWindow = window
    mWindow.setBackgroundDrawable(null)
    mWindow.setGravity(Gravity.CENTER)
    setFinishOnTouchOutside(isFinishOnTouchOutside)
}

/**
 * 保持常亮
 */
fun AppCompatActivity.addKeepScreenOn() {
    window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

/**
 * 去除常亮
 */
fun AppCompatActivity.clearKeepScreenOn() {
    window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

/**
 * 动画兼容
 */
fun <T : View> T.animator() = ViewCompat.animate(this)

/**
 * 展示toast
 */
@MainThread
fun String.showToast() = ArmsUtils.makeText(this)

/**
 * 判断是否是当前状态
 */
fun AppCompatActivity.isAtLeast(@NonNull state: Lifecycle.State) = lifecycle.currentState.isAtLeast(state)

/**
 * 获取Activity的高度
 */
fun AppCompatActivity.getHeight() = ArmsUtils.getActivityHeight(this)

/**
 * 键盘监听关闭
 */
fun AppCompatActivity.setKeyBoardListener(onSoftKeyBoardChangeListener: SoftKeyBoardListener.OnSoftKeyBoardChangeListener) {
    val softKeyBoardListener = SoftKeyBoardListener(this)
    softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener)
}

/**
 * 关闭手机的通知管理界面
 */
fun Context.collapseStatusBar() {
    try {
        @SuppressLint("WrongConstant")
        val statusBarManager = getSystemService("statusbar")
        val collapse: Method
        collapse = statusBarManager.javaClass.getMethod("collapsePanels")
        collapse.invoke(statusBarManager)
    } catch (localException: Exception) {
        localException.printStackTrace()
    }
}

/**
 * 针对6.0动态请求权限问题,判断是否允许此权限
 */
fun Context.hasPermission(vararg permissions: String,isRequest: Boolean = false): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (isRequest){
                ActivityCompat.requestPermissions(this as Activity,permissions,100)
            }
            return false
        }
    }
    return true
}

inline fun runOnMain(crossinline run: () -> Unit = {}) {
    TaskExecutor.executeOnMain(Runnable { run() })
}

inline fun runOnIo(crossinline run: () -> Unit = { }) {
    Schedulers.io().scheduleDirect { run() }
}

fun isMainThread(): Boolean {
    return Looper.getMainLooper().thread === Thread.currentThread()
}

//用户滑动最小距离
fun Context.getScaledTouch() = ViewConfiguration.get(this).scaledTouchSlop


//拦截回退按钮
inline fun onBackKeyUp(keyCode: Int, @NonNull event: KeyEvent,
                       crossinline onBack: () -> Boolean = { true }): Boolean {
    if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)
            && event.isTracking
            && !event.isCanceled) {
        if (onBack()) {
            return true
        }
    }
    return false
}


inline fun Context.createSimDialog(@LayoutRes layoutId: Int,
                                   crossinline viewListener: ((view: View) -> Unit) = {},
                                   crossinline setWindowSizeChange: ((win: Window?) -> Unit) = {
                                       it?.setLayout(ArmsUtils.dip2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT);
                                   }): AlertDialog {
    val view = LayoutInflater.from(this).inflate(layoutId, null)
    viewListener.invoke(view)
    return AlertDialog.Builder(this)
            .setView(view)
            .show().apply {
                setWindowSizeChange.invoke(window)
            }
}

/**
 * 获取网络状态监听回调
 */
fun Context.requestNetWork(callbacks: ConnectivityManager.NetworkCallback) {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val builder = NetworkRequest.Builder()
    val request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    cm.requestNetwork(request, callbacks)
}

fun Context.getFileProvider():String{
    return "${packageName}.FileProvider"
}