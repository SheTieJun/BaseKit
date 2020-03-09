package me.shetj.base.constant

import android.view.Gravity
import androidx.annotation.IntDef

interface Constant {
    //用来确定放心
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(Gravity.TOP,  Gravity.BOTTOM, Gravity.START, Gravity.END)
    annotation class GravityType


}