package me.shetj.base.ktx

import android.view.Menu
import androidx.core.view.forEach


fun Menu?.setVisible(isVisible: Boolean){
    this?.forEach {
        it.isVisible = isVisible
    }
}