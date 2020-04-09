package me.shetj.base.constant

import android.view.Gravity
import androidx.annotation.IntDef

interface Constant {
    //用来确定方向
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(Gravity.TOP,  Gravity.BOTTOM, Gravity.START, Gravity.END)
    annotation class GravityType


    companion object{
        val KEY_IS_OUTPUT_HTTP = "key_is_output_http";
        val KEY_IS_OUTPUT_LOG = "key_is_output_log";

        val KEY_SETTING_INFO = "key_setting_info";
    }
}