package shetj.me.base.test

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import me.shetj.base.base.UIState


class TestKotlin {



    companion object {


          val liveDataA = MediatorLiveData<Boolean>()
          val liveDataB1 = MediatorLiveData<Boolean>()
          val liveDataB2 = MutableLiveData<Boolean>()
          val liveDataC1 = MutableLiveData<Boolean>()
          val liveDataC2 = MutableLiveData<Boolean>()

        /**
         * [UIState]
         */
        private val _uiState = MutableStateFlow<UIState>(UIState.Loading)


        private val _testStatus = MutableSharedFlow<Int>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {

            //
            _uiState.shareIn(
                this,   //用于共享数据流的 CoroutineScope。此作用域函数的生命周期应长于任何使用方，以使共享数据流在足够长的时间内保持活跃状态。
                replay = 1, //要重放 (replay) 至每个新收集器的数据项数量。
                started = SharingStarted.WhileSubscribed() //“启动”行为政策。
            )

            _uiState.compareAndSet(UIState.Loading,UIState.End)

            _testStatus.resetReplayCache()//清空前面的值 ,供您在不想重放已向数据流发送的最新信息


        }
    }

}