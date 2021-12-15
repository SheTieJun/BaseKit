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
                Timber.e("need add permission")
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