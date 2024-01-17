package me.shetj.base.ktx

import android.app.Activity
import android.content.Intent
import android.transition.Transition
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

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
fun Activity.getActivityOptions(
    sharedCardView: View,
    transitionNameCard: String
): ActivityOptionsCompat {
    return ActivityOptionsCompat
        .makeSceneTransitionAnimation(this, sharedCardView, transitionNameCard)
}

/**
 * 让新的Activity从一个小的范围扩大到全屏
 */
fun Activity.getActivityOptions(view: View): ActivityOptionsCompat {
    return ActivityOptionsCompat.makeScaleUpAnimation(
        view,
        // The View that the new activity is animating from
        view.width / 2,
        view.height / 2,
        // 拉伸开始的坐标
        0,
        0
    )
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
 * @param transition  = explode(),slide()(),fade
 * @param shareTransition  share view 的transition  一般为changeBound
 */
fun Fragment.setEnterTransition(transition: Transition, shareTransition: Transition) {
    enterTransition = transition
    allowEnterTransitionOverlap = true
    allowReturnTransitionOverlap = true
    sharedElementEnterTransition = shareTransition
}
