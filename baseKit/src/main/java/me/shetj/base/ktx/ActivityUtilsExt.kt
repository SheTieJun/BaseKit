package me.shetj.base.ktx

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.transition.Transition
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun Context.openActivity(scheme: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
    startActivity(intent)
}

/**
 * ABCD => (D->B) = ACDB
 * ABCBD => (D->B) = ABCDB
 */
fun Context.moveToFront(activity: Activity){
    val intent: Intent = Intent(this, activity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    }
    startActivity(intent)
}

/**
 * 通过包名，让APP到前台，前提是APP在后台了，所有如果代码无效，可能是因为APP判在前台
 */
fun ActivityManager.moveToFrontApp(packageName:String){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.appTasks?.first {
            it.taskInfo.baseIntent.component?.packageName == packageName
        }?.apply {
            moveToFront()
        }
    }
}

fun Context.openActivityByPackageName(ackageName: String) {
    val intent = packageManager.getLaunchIntentForPackage(ackageName)
    startActivity(intent)
}

fun FragmentManager.addFragmentToActivity(fragment: Fragment, frameId: Int) {
    val transaction = beginTransaction()
    transaction.add(frameId, fragment)
    transaction.commitAllowingStateLoss()
}

fun FragmentManager.removeFragment() {
    if (backStackEntryCount > 1) {
        popBackStack()
    }
}

fun FragmentManager.replaceFragment(fragment: Fragment, frameId: Int) {
    val transaction = beginTransaction().addToBackStack(null)
    transaction.replace(frameId, fragment)
    transaction.commit()
}


fun Fragment.show(supportFragmentManager: FragmentManager, @IdRes rootLayoutId: Int) {
    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
    val fragment = supportFragmentManager.findFragmentByTag(this::class.java.simpleName)
    if (fragment == null) {
        ft.add(rootLayoutId, this, this::class.java.simpleName)
    } else {
        ft.show(this)
    }
    ft.commitAllowingStateLoss()
}

fun Fragment.hide(supportFragmentManager: FragmentManager) {
    if (!isHidden) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.hide(this)
        ft.commitAllowingStateLoss()
    }
}

/**
 * Set background color for fragment.
 * @param color    The background color.
 */
fun Fragment.setBackgroundColor(@ColorInt color: Int) {
    val view = this.view
    view?.setBackgroundColor(color)
}

/**
 * Set background resource for fragment.
 */
fun Fragment.setBackgroundResource(@DrawableRes resId: Int) {
    val view = this.view
    view?.setBackgroundResource(resId)
}

/**
 * 当你需要当前界面中的某个元素和新界面中的元素有关时，你可以使用这个动画。效果很赞~！
 */
fun Activity.getActivityOptions(sharedCardView: View, TRANSITION_NAME_CARD: String): ActivityOptionsCompat {
    return ActivityOptionsCompat
            .makeSceneTransitionAnimation(this, sharedCardView, TRANSITION_NAME_CARD)
}

/**
 * 让新的Activity从一个小的范围扩大到全屏
 */
fun Activity.getActivityOptions(view: View): ActivityOptionsCompat {
    return ActivityOptionsCompat.makeScaleUpAnimation(view,
            //The View that the new activity is animating from
            view.width / 2, view.height / 2,
            //拉伸开始的坐标
            0, 0)
}

/**
 * 多个元素和新的Activity相关的情况，注意下第二个参数Pair这个键值对后面有...，标明是可以传入多个Pair对象的
 */
fun Activity.getActivityOptions(vararg arg1: Pair<View, String>): ActivityOptionsCompat {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, *arg1)
}

fun Activity.startNewActivity(options: ActivityOptionsCompat, activityClass: Class<*>) {
    val intent = Intent(this, activityClass)
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    ActivityCompat.startActivity(this, intent, options.toBundle())
}


/**
 * @param slideTransition  = explode(),slide()(),fade
 * @param shareTransition  share view 的transition  一般为changeBound
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Fragment.setEnterTransition(slideTransition: Transition, shareTransition: Transition) {
    enterTransition = slideTransition
    allowEnterTransitionOverlap = true
    allowReturnTransitionOverlap = true
    sharedElementEnterTransition = shareTransition
}

fun Context.getIdByName(className: String, resName: String): Int {
    val packageName = packageName
    return applicationContext.resources.getIdentifier(resName, className, packageName)
}


