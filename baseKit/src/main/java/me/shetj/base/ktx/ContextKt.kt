package me.shetj.base.ktx

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlin.coroutines.CoroutineContext
import me.shetj.base.BaseKit


/**
 * 获取上下文的 CoroutineContext
 *
 * - Context:
 *   - 1.Service： return BaseKit.applicationScope
 *   - 2.Application ：return  BaseKit.applicationScope
 *   - 3.Activity ：return lifecycleScope.CoroutineContext
 *
 * 这里需要判断context 是不是  ComponentActivity
 *
 * - [androidx.lifecycle.Lifecycle.mInternalScopeRef]
 * 如果不是，我们需要去循环查找找
 */
val Context.lifeScope: CoroutineContext
    get() {
        if (this is ComponentActivity) {
            return this.lifecycleScope.coroutineContext
        }
        var context = this
        while (context is ContextWrapper) {
            if (context is ComponentActivity) {
                return context.lifecycleScope.coroutineContext
            }
            context = context.baseContext
        }
        return BaseKit.applicationScope
    }

fun Context.getIdByName(className: String, resName: String): Int {
    val packageName = packageName
    return applicationContext.resources.getIdentifier(resName, className, packageName)
}

fun Context.openActivity(scheme: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
    startActivity(intent)
}


fun Context.openActivityByPackageName(ackageName: String) {
    val intent = packageManager.getLaunchIntentForPackage(ackageName)
    startActivity(intent)
}

/**
 *  - 让APP到前台，前提是APP已经后台了
 *  - 如果代码无效，可能是因为APP被判定在前台
 */
fun Context.moveToFrontApp() {
    (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)?.moveToFrontApp(packageName)
}


/**
 * - 通过包名，让APP到前台，前提是APP在后台了
 * - 如果代码无效，可能是因为APP被判定在前台
 */
fun ActivityManager.moveToFrontApp(packageName: String) {
    this.appTasks?.first {
        it.taskInfo.baseIntent.component?.packageName == packageName
    }?.apply {
        moveToFront()
    }
}

/**
 * - ABCD => B = ACDB
 * - ABCBD => B = ABCDB
 * 只把上一界面提前第一个
 */
fun Context.moveToFront(activity: Activity) {
    val intent: Intent = Intent(this, activity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    }
    startActivity(intent)
}
