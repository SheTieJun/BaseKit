package shetj.me.base.livedate_test

import android.os.Looper
import me.shetj.base.base.AbLifecycleWithCopeComponent


class LiveDemo : AbLifecycleWithCopeComponent() {


    init {

        Thread(){
            Looper.myLooper()
        }
    }


    fun start(){
        onStart()
    }


}