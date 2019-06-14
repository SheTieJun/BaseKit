package shetj.me.base.qmui

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityOptions
import android.os.Build
import android.os.Looper
import androidx.fragment.app.FragmentManager

import com.qmuiteam.qmui.QMUILog

/**
 * Created by Chaojun Wang on 6/9/14.
 */
object Utils {

    /**
     * Convert a translucent themed Activity
     * [android.R.attr.windowIsTranslucent] to a fullscreen opaque
     * Activity.
     *
     *
     * Call this whenever the background of a translucent Activity has changed
     * to become opaque. Doing so will allow the [android.view.Surface] of
     * the Activity behind to be released.
     *
     *
     * This call has no effect on non-translucent activities or on activities
     * with the [android.R.attr.windowIsFloating] attribute.
     */
    fun convertActivityFromTranslucent(activity: Activity) {
        try {
            @SuppressLint("PrivateApi") val method = Activity::class.java.getDeclaredMethod("convertFromTranslucent")
            method.isAccessible = true
            method.invoke(activity)
        } catch (ignore: Throwable) {
        }

    }

    /**
     * Convert a translucent themed Activity
     * [android.R.attr.windowIsTranslucent] back from opaque to
     * translucent following a call to
     * [.convertActivityFromTranslucent] .
     *
     *
     * Calling this allows the Activity behind this one to be seen again. Once
     * all such Activities have been redrawn
     *
     *
     * This call has no effect on non-translucent activities or on activities
     * with the [android.R.attr.windowIsFloating] attribute.
     */
    fun convertActivityToTranslucent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertActivityToTranslucentAfterL(activity)
        } else {
            convertActivityToTranslucentBeforeL(activity)
        }
    }

    /**
     * Calling the convertToTranslucent method on platforms before Android 5.0
     */
    private fun convertActivityToTranslucentBeforeL(activity: Activity) {
        try {
            val classes = Activity::class.java.declaredClasses
            var translucentConversionListenerClazz: Class<*>? = null
            for (clazz in classes) {
                if (clazz.simpleName.contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz
                }
            }
            @SuppressLint("PrivateApi") val method = Activity::class.java.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz!!)
            method.isAccessible = true
            method.invoke(activity, null)
        } catch (ignore: Throwable) {
        }

    }

    /**
     * Calling the convertToTranslucent method on platforms after Android 5.0
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun convertActivityToTranslucentAfterL(activity: Activity) {
        try {
            @SuppressLint("PrivateApi") val getActivityOptions = Activity::class.java.getDeclaredMethod("getActivityOptions")
            getActivityOptions.isAccessible = true
            val options = getActivityOptions.invoke(activity)

            val classes = Activity::class.java.declaredClasses
            var translucentConversionListenerClazz: Class<*>? = null
            for (clazz in classes) {
                if (clazz.simpleName.contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz
                }
            }
            @SuppressLint("PrivateApi") val convertToTranslucent = Activity::class.java.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz, ActivityOptions::class.java)
            convertToTranslucent.isAccessible = true
            convertToTranslucent.invoke(activity, null, options)
        } catch (ignore: Throwable) {
        }

    }

    fun assertInMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            val elements = Thread.currentThread().stackTrace
            var methodMsg: String? = null
            if (elements != null && elements.size >= 4) {
                methodMsg = elements[3].toString()
            }
            throw IllegalStateException("Call the method must be in main thread: " + methodMsg!!)
        }
    }

    internal fun findAndModifyOpInBackStackRecord(fragmentManager: FragmentManager?, backStackIndex: Int, handler: OpHandler?) {
        var backStackIndex = backStackIndex
        if (fragmentManager == null || handler == null) {
            return
        }
        val backStackCount = fragmentManager.backStackEntryCount
        if (backStackCount > 0) {
            if (backStackIndex >= backStackCount || backStackIndex < -backStackCount) {
                QMUILog.d("findAndModifyOpInBackStackRecord", "backStackIndex error: " +
                        "backStackIndex = " + backStackIndex + " ; backStackCount = " + backStackCount)
                return
            }
            if (backStackIndex < 0) {
                backStackIndex += backStackCount
            }
            try {
                val backStackEntry = fragmentManager.getBackStackEntryAt(backStackIndex)

                val opsField = backStackEntry.javaClass.getDeclaredField("mOps")
                opsField.isAccessible = true
                val opsObj = opsField.get(backStackEntry)
                if (opsObj is List<*>) {
                    for (op in opsObj) {
                        if (handler.handle(op!!)) {
                            return
                        }
                    }
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }

      interface OpHandler {
        fun handle(op: Any): Boolean
    }
}
