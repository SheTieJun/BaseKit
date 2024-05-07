package me.shetj.base.ktx

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
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
import androidx.core.content.PermissionChecker
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import me.shetj.base.R
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.model.NetWorkLiveDate
import me.shetj.base.tools.app.ArmsUtils
import java.io.File
import java.lang.reflect.Method
import kotlin.math.pow
import kotlin.math.sqrt

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

inline fun <reified T : Activity> Context.launchActivity(crossinline func: (Intent.() -> Unit) = {}) {
    val intent = Intent(this, T::class.java).apply(func)
    startActivity(intent)
}

fun FragmentActivity.grayThemChange(isGrayTheme: Boolean) {
    val decorView = window?.decorView
    val isMourn = (decorView?.getTag(R.id.isGrayTheme) as? Boolean) ?: false
    if (isGrayTheme != isMourn) {
        if (isGrayTheme) {
            decorView?.setTag(R.id.isGrayTheme, true)
            decorView?.setLayerType(View.LAYER_TYPE_HARDWARE, GrayThemeLiveData.getInstance().getSatPaint(0f))
        } else {
            decorView?.setTag(R.id.isGrayTheme, false)
            decorView?.setLayerType(View.LAYER_TYPE_NONE, null)
        }
    }
}

fun isPad(context: Context): Boolean {
    val isPad = (
        context.resources.configuration.screenLayout
            and Configuration.SCREENLAYOUT_SIZE_MASK
        ) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    val dm = Resources.getSystem().displayMetrics
    val x = (dm.widthPixels / dm.xdpi).toDouble().pow(2.0)
    val y = (dm.heightPixels / dm.ydpi).toDouble().pow(2.0)
    val screenInches = sqrt(x + y) // 屏幕尺寸
    return isPad || screenInches >= 7.0
}

fun View?.setLayer(isMourn: Boolean) {
    this ?: return
    val isCurMourn = (getTag(R.id.isGrayTheme) as? Boolean) ?: false
    if (isMourn != isCurMourn) {
        if (isMourn) {
            setTag(R.id.isGrayTheme, true)
            setLayerType(View.LAYER_TYPE_HARDWARE, GrayThemeLiveData.getInstance().getSatPaint(0f))
        } else {
            setTag(R.id.isGrayTheme, false)
            setLayerType(View.LAYER_TYPE_NONE, null)
        }
    }
}

/**
 * Disable secure
 * 禁止录屏
 */
fun FragmentActivity.disableSecure() {
    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
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
 * 展示toast
 */
@MainThread
fun String.showToast() = ArmsUtils.makeText(this)

/**
 * Show toast to do
 * 用来记录需要完成的功能，点击时候打印日志出来
 */
inline fun <reified T> String.showToDoToast() {
    (T::class.java.simpleName + ":" + this).logI("TODO")
    ArmsUtils.makeText(T::class.java.simpleName + ":" + this)
}

@JvmOverloads
fun Activity.showSnack(msg: String, view: View? = null) {
    Snackbar.make(view ?: findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
}

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
    val permissionsCheck: MutableList<String> = ArrayList()
    for (permission in permissions) {
        if (PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsCheck.add(permission)
        }
    }
    if (permissionsCheck.isEmpty()) return true
    if (isRequest) {
        ActivityCompat.requestPermissions(this, permissionsCheck.toTypedArray(), 100)
    }
    return false
}

/**
 * Need permission tip
 *
 * @param permissions
 * @return true:需要提示
 */
fun Activity.needPermissionTip(@NonNull permissions: String): Boolean {
    /**
     * 从来没有申请过:
     * ActivityCompat.shouldShowRequestPermissionRationale=false;
     *
     * 第一次请求权限被禁止，但未选择【不再提醒】
     * ActivityCompat.shouldShowRequestPermissionRationale = true;
     *
     * 允许权限后
     * ActivityCompat.shouldShowRequestPermissionRationale=false;
     *
     * 禁止权限，并选中【禁止后不再询问】
     * ActivityCompat.shouldShowRequestPermissionRationale=false；
     */
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permissions)
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

fun FragmentActivity.onBackGoHome() {
    try {
        val i = Intent(Intent.ACTION_MAIN)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.addCategory(Intent.CATEGORY_HOME)
        startActivity(i)
    } catch (e: Exception) {
        onBackPressedDispatcher.onBackPressed()
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
    crossinline onViewCreated: ((mVB: VB,dialog: AlertDialog) -> Unit) = {_,_-> },
    crossinline setWindowSizeChange: ((dialog: AlertDialog, window: Window?) -> Unit) = { _, window ->
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }
): AlertDialog? {
    val mVB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, LayoutInflater.from(this)) as VB
    return AlertDialog.Builder(this)
        .setView(mVB.root)
        .show()?.apply {
            onViewCreated.invoke(mVB,this)
            setWindowSizeChange.invoke(this, this.window)
        }
}
/**
 * 获取网络状态监听回调
 * - tip:调用次数不宜过多，部分高版本手机会崩溃
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
                NetWorkLiveDate.getInstance().setNetType(NetWorkLiveDate.NetType.UNKNOWN)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                NetWorkLiveDate.getInstance().onLost()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                NetWorkLiveDate.getInstance().onLost()
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
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
                            NetWorkLiveDate.getInstance().setNetType(NetWorkLiveDate.NetType.UNKNOWN)
                        }
                    }
                }
            }
        }
    )
}

fun Context.getFileProviderAuthority(): String {
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
        context,
        arrayOf(file.toString()),
        arrayOf("image/jpeg")
    ) { path, uri ->
        ("扫描完成 path: $path, uri: $uri").logI()
    }
}

fun Context.startIgnoreBatteryOpt() {
    val i = Intent()
    i.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    // data为应用包名
    i.data = Uri.parse("package:$packageName")
    startActivity(i)
}

/**
 * 是否忽略电池优化
 * @return
 */
fun Context.isIgnoringPower(): Boolean {
    val powerManager: PowerManager? = getSystemService()
    return powerManager?.isIgnoringBatteryOptimizations(packageName) ?: false
}
