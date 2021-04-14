package me.shetj.base.ktx

import android.app.Dialog
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

inline fun <reified VB: ViewBinding> AppCompatActivity.viewBind(): Lazy<VB> {
    return getViewBind(layoutInflater)
}


inline fun <reified VB: ViewBinding> Dialog.viewBind(): Lazy<VB> {
    return getViewBind(layoutInflater)
}


inline fun <reified VB: ViewBinding> Fragment.viewBind(): Lazy<VB> {
    return getViewBind(layoutInflater)
}

inline fun <reified VB: ViewBinding> getViewBind(layoutInflater: LayoutInflater): Lazy<VB> {
    return object :Lazy<VB>{
        private var cached: VB? = null
        override val value: VB
            get()  {
                return cached?:(VB::class.java.getMethod("inflate", LayoutInflater::class.java)
                        .invoke(null, layoutInflater) as VB).also {
                    cached = it
                }
            }

        override fun isInitialized(): Boolean {
            return cached != null
        }
    }
}


