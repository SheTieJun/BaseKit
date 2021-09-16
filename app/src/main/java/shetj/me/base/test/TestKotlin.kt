package shetj.me.base.test

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.runBlocking


class TestKotlin {



    companion object {


          val liveDataA = MediatorLiveData<Boolean>()
          val liveDataB1 = MediatorLiveData<Boolean>()
          val liveDataB2 = MutableLiveData<Boolean>()
          val liveDataC1 = MutableLiveData<Boolean>()
          val liveDataC2 = MutableLiveData<Boolean>()

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {


        }
    }

}