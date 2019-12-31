package me.shetj.base.constant

import androidx.annotation.IntDef

interface Constant {

    companion object{
        const val LEFT = 1
        const val TOP = 2
        const val RIGHT = 3
        const val BOTTOM = 4
    }

    @IntDef( TOP, LEFT, RIGHT , BOTTOM )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class DrawableDirection
}