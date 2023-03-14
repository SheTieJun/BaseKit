package me.shetj.base.ktx

import android.content.Context
import android.content.res.Resources
import android.transition.Transition
import android.transition.TransitionInflater

/**
 * 通过资源id 获取Transition
 */
fun Context.getTransition(resource: Int): Transition? {
    return try {
        TransitionInflater.from(this).inflateTransition(resource)
    } catch (e: Resources.NotFoundException) {
        null
    }
}
