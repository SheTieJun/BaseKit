package me.shetj.base.tools.app

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import timber.log.Timber


object FloatKit {

    private const val OP_SYSTEM_ALERT_WINDOW = 24 // 支持TYPE_TOAST悬浮窗

    fun Context.getWinManager(): WindowManager {
        return applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun getWindowParams(): WindowManager.LayoutParams {
        val mWindowParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        mWindowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        mWindowParams.format = PixelFormat.TRANSLUCENT
        mWindowParams.gravity = Gravity.START or Gravity.TOP
        return mWindowParams
    }

    fun Context.checkFloatPermission(needGet: Boolean = false): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 6.0动态申请悬浮窗权限
            if (!Settings.canDrawOverlays(this)) {
                if (needGet) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:$packageName")
                    if (this is FragmentActivity) {
                        startActivityForResult(intent, 0)
                    } else {
                        startActivity(intent)
                    }
                }
                return false
            }
        } else {
            if (!checkOp(this, OP_SYSTEM_ALERT_WINDOW)) {
                return false
            }
        }
        return true
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun checkOp(context: Context, op: Int): Boolean {
        val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val method = AppOpsManager::class.java.getDeclaredMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            return AppOpsManager.MODE_ALLOWED == method.invoke(
                manager,
                op,
                Binder.getCallingUid(),
                context.packageName
            ) as Int
        } catch (e: Exception) {
            Timber.e(Log.getStackTraceString(e))
        }
        return true
    }
}