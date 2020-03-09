package me.shetj.base.kt

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.app.SoftKeyBoardListener
import java.lang.reflect.Method


/**
 * 用来防止重新进入的时候多次展示 Splash
 * 是否是栈的底部
 */
fun AppCompatActivity.isRoot(){
    if (!isTaskRoot){
        finish()
    }
}

@JvmOverloads
fun Context.start(activity : Class<*>,isFinish :Boolean = false){
    ArmsUtils.startActivity(this as AppCompatActivity,activity)
    if (isFinish){
        finish()
    }
}

@JvmOverloads
fun Context.start(intent: Intent,isFinish: Boolean = false){
    ArmsUtils.startActivity(this as AppCompatActivity,intent)
    if (isFinish){
        finish()
    }
}

/**
 * @param isFinishOnTouchOutside 是否点击window 关闭activity
 */
@JvmOverloads
fun AppCompatActivity.cleanBackground(isFinishOnTouchOutside: Boolean = true){
    val mWindow = window
    mWindow.setBackgroundDrawable(null)
    mWindow.setGravity(Gravity.CENTER)
    setFinishOnTouchOutside(isFinishOnTouchOutside)
}

/**
 * 保持常亮
 */
fun AppCompatActivity.addKeepScreenOn() {
    if (window == null) return
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

/**
 * 去除常亮
 */
fun AppCompatActivity.clearKeepScreenOn() {
    if (window == null) return
    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

/**
 * 动画兼容
 */
fun <T:View> T.animator() = ViewCompat.animate(this)

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
fun Context.collapseStatusBar( ) {
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

fun AppCompatActivity.getRxPermissions() = RxPermissions(this)

fun Fragment.getRxPermissions() = RxPermissions(this)

/**
 * 针对6.0动态请求权限问题,判断是否允许此权限
 */
fun Context.hasPermission(vararg permissions: String): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

fun runOnMain(run:()->Unit = {}){
    AndroidSchedulers.mainThread().scheduleDirect { run() }
}

fun runOnIo(run:()->Unit = { }){
    Schedulers.io().scheduleDirect {run() }
}


