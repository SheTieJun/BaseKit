

package shetj.me.base.test

import androidx.core.text.buildSpannedString
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import me.shetj.base.ktx.renderType
import me.shetj.base.model.UIState


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
            replay = 1, //至少1
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

//        @JvmStatic
//        fun main(args: Array<String>) = runBlocking {
//            liveDataA.addSource(liveDataB1) {
//                liveDataA.postValue(liveDataB1.isTrue()|| liveDataB2.isTrue() )
//            }
//
//
//
//
//            //
//            _uiState.shareIn(
//                this,   //用于共享数据流的 CoroutineScope。此作用域函数的生命周期应长于任何使用方，以使共享数据流在足够长的时间内保持活跃状态。
//                replay = 1, //要重放 (replay) 至每个新收集器的数据项数量。
//                started = SharingStarted.WhileSubscribed() //“启动”行为政策。
//            )
//
//            _uiState.compareAndSet(UIState.Loading, UIState.End)
//
//            _testStatus.resetReplayCache()//清空前面的值 ,供您在不想重放已向数据流发送的最新信息
//
//
//            var dets by Delegates.observable("默认值"){
//                    property, oldValue, newValue ->
//
//            }
//        }

        @JvmStatic
        fun main(args: Array<String>) {
           println(System.currentTimeMillis().toInt())
        }

        /**
        *@[测试]
        */
        fun testBuild(){

            //1.6.0
            buildList<String> {

            }.isEmpty()

            buildMap<String,String> {

            }

            buildSet<String> {

            }
            //end 1.6.0

            buildString {

            }

            buildSpannedString {

            }

            buildString(1) {

            }

            5000.toDuration(DurationUnit.SECONDS).apply {
                this.inWholeDays
            }.toDouble(DurationUnit.HOURS)

        }


        fun testLiveData(){

            liveDataA.map {  }

        }




        @ExperimentalStdlibApi
        fun typeofTest(){
           val string =  renderType<String>()
            print(string)
        }
    }





    fun openMini() {
//        val req = WXLaunchMiniProgram.Req()
//        req.userName = "gh_252c5f06840b" // 填小程序原始id
//        req.path = "pages/detail/detail.html?url=https://docs.qq.com/doc/DYU5oU21hUE5LSFZM"
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE // 可选打开 开发版，体验版和正式版
//        mWXApi?.sendReq(req)
    }
}

//fun main() {
//
//    method{
//        return@method 1
//        return
//    }
//
//    method2 {
//        return@method2  1
//        return
//    }
//
//    method3 {
//        return@method3  3
//        return
//    }
//
//}
//
//
//
//fun method(t: (Int) -> Int) {
//    t.invoke(1)
//}
//
//inline fun method2(crossinline t: (Int) -> Int) {
//    t.invoke(2)
//}
//
//inline fun method3( t: (Int) -> Int) {
//    t.invoke(3)
//}