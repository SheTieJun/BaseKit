package me.shetj.base.kt

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Context.openActivity(scheme: String){
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scheme))
    startActivity(intent)
}

fun Context.openActivityByPackageName(ackageName: String){
    val intent =  packageManager.getLaunchIntentForPackage(ackageName)
    startActivity(intent)
}

fun FragmentManager.addFragmentToActivity(fragment: Fragment, frameId: Int){
    val transaction = beginTransaction()
    transaction.add(frameId, fragment)
    transaction.commit()
}
fun FragmentManager.removeFragment( ){
    if ( backStackEntryCount > 1) {
        popBackStack()
    }
}

fun FragmentManager.replaceFragment(fragment: Fragment, frameId: Int) {
    val transaction = beginTransaction().addToBackStack(null)
    transaction.replace(frameId, fragment)
    transaction.commit()
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
 *
 * @param fragment The fragment.
 * @param resId    The resource id.
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
fun Activity.getActivityOptions(  vararg arg1: Pair<View, String>): ActivityOptionsCompat {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, *arg1)
}

fun Activity.startNewAcitivity( options: ActivityOptionsCompat, activityClass: Class<*>) {
    val intent = Intent(this, activityClass)
    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    ActivityCompat.startActivity(this, intent, options.toBundle())
}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Activity.setEnterTransition( type: String) {
    when (type) {
        "explode" -> {
            val explode = Explode()
            explode.duration = 500L
            window.enterTransition = explode
        }
        "slide" -> {
            val slide = Slide(Gravity.BOTTOM)
            slide.duration = 500L
            window.enterTransition = slide
        }
        "fade" -> {
            val fade = Fade()
            fade.duration = 500L
            window.enterTransition = fade
        }
        else -> {
        }
    }
}

/**
 * @param slideTransition  = explode(),slide()(),fade
 * @param transition  share view 的transition  一般为changeBound
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Fragment.setEnterTransition(  slideTransition: Transition, transition: Transition) {
    enterTransition = slideTransition
    allowEnterTransitionOverlap = true
    allowReturnTransitionOverlap = true
    sharedElementEnterTransition = transition
}

fun Context.getIdByName (className: String, resName: String): Int {
    val packageName = packageName
    return applicationContext.resources.getIdentifier(resName, className, packageName)
}

