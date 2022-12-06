/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.ktx

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import java.io.File
import java.lang.reflect.Method
import me.shetj.base.R
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.tools.app.ArmsUtils

/**
 * 用来防止重新进入的时候多次展示 Splash
 * 是否是栈的底部
 */
fun AppCompatActivity.isRoot(): Boolean {
    if (!isTaskRoot) {
        finish()
        return true
    }
    return false
}

@JvmOverloads
inline fun <reified T : Activity> Context.start(isFinish: Boolean = false) {
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

fun FragmentActivity.grayThemChange(isGrayTheme:Boolean){
    val decorView = window?.decorView
    val isMourn = (decorView?.getTag(R.id.isGrayTheme) as? Boolean )?:false
    if (!isMourn){
        if (isGrayTheme){
            decorView?.setTag(R.id.isGrayTheme,true)
            decorView?.setLayerType(View.LAYER_TYPE_HARDWARE, GrayThemeLiveData.getInstance().getSatPaint(0f))
        }
    }else{
        if (!isGrayTheme){
            decorView?.setTag(R.id.isGrayTheme,false)
            decorView?.setLayerType(View.LAYER_TYPE_NONE, null)
        }
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
fun AppCompatActivity.isAtLeast(@NonNull state: Lifecycle.State) =
    lifecycle.currentState.isAtLeast(state)

/**
 * 获取Activity的高度
 */
fun AppCompatActivity.getHeight() = ArmsUtils.getActivityHeight(this)

/**
 * 关闭手机的通知管理界面
 */
fun Context.collapseStatusBar() {
    try {
        @SuppressLint("WrongConstant")
        val statusBarManager = getSystemService("statusbar")
        val collapse: Method = statusBarManager.javaClass.getMethod("collapsePanels")
        collapse.invoke(statusBarManager)
    } catch (localException: Exception) {
        localException.printStackTrace()
    }
}

/**
 * 针对6.0动态请求权限问题,判断是否允许此权限
 *  可以使用 [AppCompatActivity.registerForActivityResult] 替代
 *  ```
 *  registerForActivityResult(ActivityResultContracts.RequestPermission())
 *  ```
 */
fun Activity.hasPermission(
    vararg permissions: String,
    isRequest: Boolean = true
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
    val permissionsCheck: MutableList<String> = ArrayList()
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsCheck.add(permission)
        }
    }
    if (permissionsCheck.isEmpty()) return true
    if (isRequest) {
        ActivityCompat.requestPermissions(this, permissionsCheck.toTypedArray(), 100)
    }
    return false
}

fun AppCompatActivity.onRequestPermissionsResultImpl(
    @NonNull permissions: Array<String>,
    @NonNull grantResults: IntArray
): String {
    val sb = StringBuilder()
    for (i in grantResults.indices) {
        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                when (permissions[i]) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> sb.append("\n · 读写存储  ")
                    Manifest.permission.CAMERA -> sb.append("\n · 相机  ")
                    Manifest.permission.READ_PHONE_STATE -> sb.append("\n · 电话状态  ")
                    Manifest.permission.RECORD_AUDIO -> sb.append("\n · 麦克风录制  ")
                    Manifest.permission.WRITE_CALENDAR -> sb.append("\n · 添加日程  ")
                }
            }
        }
    }
    return sb.toString()
}

fun isMainThread(): Boolean {
    return Looper.getMainLooper().thread === Thread.currentThread()
}

// 用户滑动最小距离
fun Context.getScaledTouch() = ViewConfiguration.get(this).scaledTouchSlop

// 拦截回退按钮
inline fun onBackKeyUp(
    keyCode: Int,
    @NonNull event: KeyEvent,
    crossinline onBack: () -> Boolean = { true }
): Boolean {
    if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) &&
        event.isTracking &&
        !event.isCanceled
    ) {
        if (onBack()) {
            return true
        }
    }
    return false
}

fun Activity.onBackGoHome() {
    try {
        val i = Intent(Intent.ACTION_MAIN)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.addCategory(Intent.CATEGORY_HOME)
        startActivity(i)
    } catch (e: Exception) {
        onBackPressed()
    }
}

inline fun Context.createSimDialog(
    @LayoutRes layoutId: Int,
    crossinline viewListener: ((view: View) -> Unit) = {},
    crossinline setWindowSizeChange: ((win: Window?) -> Unit) = {
        it?.setLayout(ArmsUtils.dp2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT)
    }
): AlertDialog {
    val view = LayoutInflater.from(this).inflate(layoutId, null)
    viewListener.invoke(view)
    return AlertDialog.Builder(this)
        .setView(view)
        .show().apply {
            setWindowSizeChange.invoke(window)
        }
}

inline fun <reified VB : ViewBinding> Context.createSimDialog(
    crossinline viewListener: ((mVB: VB) -> Unit) = { },
    crossinline setWindowSizeChange: ((win: Window?) -> Unit) = {
        it?.setLayout(ArmsUtils.dp2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT)
    }
): AlertDialog? {
    val mVB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, LayoutInflater.from(this)) as VB
    viewListener.invoke(mVB)
    return AlertDialog.Builder(this)
        .setView(mVB.root)
        .show()?.apply {
            setWindowSizeChange.invoke(window)
        }
}

/**
 * 获取网络状态监听回调
 */
@RequiresPermission(allOf = ["android.permission.CHANGE_NETWORK_STATE"])
internal fun Context.requestNetWork() {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val builder = NetworkRequest.Builder()
    val request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    cm.requestNetwork(
        request,
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                NetWorkLiveDate.getInstance().setNetType(NetWorkLiveDate.NetType.NONE)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                NetWorkLiveDate.getInstance().onLost()
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {

                    when {
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            NetWorkLiveDate.getInstance().setNetType(NetWorkLiveDate.NetType.WIFI)
                        }
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            NetWorkLiveDate.getInstance().setNetType(NetWorkLiveDate.NetType.PHONE)
                        }
                        else -> {
                            NetWorkLiveDate.getInstance().setNetType(NetWorkLiveDate.NetType.NONE)
                        }
                    }
                }
            }
        }
    )
}

fun Context.getFileProvider(): String {
    return "$packageName.FileProvider"
}

fun Activity.getWindowContent(): FrameLayout? {
    val rootView = window.decorView.rootView
    return rootView.findViewById(android.R.id.content)
}

/**
 * Works with file:// URIs from primary storage
 * Not works with file:// URIs from secondary storage (such as removable storage)
 * Not works with any content:// URI
 */
 fun refreshAlbum(context: Context, fileUri: String) {
    val file = File(fileUri)
    MediaScannerConnection.scanFile(
        context, arrayOf(file.toString()),
        arrayOf("image/jpeg")
    ) { path, uri ->
        ("扫描完成 path: $path, uri: $uri").logI()
    }
}





