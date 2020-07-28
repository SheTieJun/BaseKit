package shetj.me.base.livedate_test

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.shetj.base.ktx.toJson
import shetj.me.base.bean.MusicBean
import timber.log.Timber


class LiveDemo : LifecycleOwner {
    val registry = LifecycleRegistry(this)
    val scope = CoroutineScope(Dispatchers.Main)


    fun onStart(){
        //是否必须有ON_START 才可以
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun testLiveData() {

        val live = object :LiveData<MusicBean>(){
           public override fun setValue(value: MusicBean?) {
                super.setValue(value)
            }
        }

        live.observe(this, Observer {
            Timber.i(it.toJson())
        })

        live.value = MusicBean().apply {
            this.title = "music2"
        }
        live.value = null
    }

    override fun getLifecycle(): Lifecycle {

        return registry

    }


    fun onDestroy(){
        //没有设置其他状态？ 是否可以设置ON_DESTROY
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }


}