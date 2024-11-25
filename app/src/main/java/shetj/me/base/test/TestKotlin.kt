package shetj.me.base.test

import android.util.Log
import androidx.core.text.buildSpannedString
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.renderType
import me.shetj.base.model.UIState
import shetj.me.base.utils.KeyStoreKit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
            replay = 1, // 至少1
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
            val mTtsSynthesisText: MutableList<String> = mutableListOf()
            val ttsText =
                "你的发音流利度和准确度表现不错。但在发音完整度方面存在问题，有字漏读的情况。希望你下次能完整地读出每个字。你很有潜力，只要坚持练习就会有很大进步，再听一次示范音然后再练一次吧。"

            // 使用下面几个标点符号来分句，会让通过 MESSAGE_TYPE_TTS_PLAYBACK_PROGRESS 返回的播放进度更加准确
            val tmp = ttsText.split("[;|!|?|。|！|？|；|…]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (j in tmp.indices) {
                val tmp = tmp[j].trim { it <= ' ' }
                if (tmp.isNotEmpty()) {
                    mTtsSynthesisText.add(tmp)
                }
            }


            var mTtsSynthesisIndex = 0
            val synthesisIndex = mTtsSynthesisIndex
            val mTtsPlayingIndex = 0
            val mTtsPlayingProgress = 0.5

            val maxSentencesDisplayed = mTtsSynthesisText.size
            val beginIndex = max(mTtsPlayingIndex.toDouble(), 0.0).toInt()
            val stringBuilder = StringBuilder()
            var playingBeginIndex = 0
            var playingEndIndex = 0
            for (cnt in 0 until maxSentencesDisplayed) {
                val index = (beginIndex + cnt) % mTtsSynthesisText.size
                if (index == mTtsPlayingIndex) {
                    playingBeginIndex = stringBuilder.length
                }
                stringBuilder.append(mTtsSynthesisText[index]).append("\n")
                if (index == mTtsPlayingIndex) {
                    playingEndIndex = (min(
                        ((ceil(mTtsSynthesisText[index].length * mTtsPlayingProgress)).toInt()).toDouble(),
                        mTtsSynthesisText[index].length.toDouble()
                    ) + playingBeginIndex).toInt()
                }
            }
            println("$stringBuilder,$playingBeginIndex, $playingEndIndex,")
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun test() {
        var strObject: String
        initStr {
            strObject = "This field must be initialized"
        }
        strObject.length // 编译错误，编译器不知道 strObject 是否有被初始化
    }


    @ExperimentalContracts
    fun initStr(block: () -> Unit) {
        //至少执行一次
        contract { callsInPlace(block, kotlin.contracts.InvocationKind.AT_LEAST_ONCE) }
        block()
    }


    /**
     *@[测试]
     */
    fun testBuild() {


        buildMap<String, String> {
        }

        buildSet<String> {
        }
        // end 1.6.0

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

    fun testLiveData() {

    }

    @ExperimentalStdlibApi
    fun typeofTest() {
        val string = renderType<String>()
        print(string)
    }


    class Site(map: MutableMap<String, Any?>) {
        val name: String by map
        val url: String by map
    }
}

//fun openMini() {
//        val req = WXLaunchMiniProgram.Req()
//        req.userName = "gh_252c5f06840b" // 填小程序原始id
//        req.path = "pages/detail/detail.html?url=https://docs.qq.com/doc/DYU5oU21hUE5LSFZM"
//        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE // 可选打开 开发版，体验版和正式版
//        mWXApi?.sendReq(req)
//}

// fun main() {
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
// }
//
//
//
// fun method(t: (Int) -> Int) {
//    t.invoke(1)
// }
//
// inline fun method2(crossinline t: (Int) -> Int) {
//    t.invoke(2)
// }
//
// inline fun method3( t: (Int) -> Int) {
//    t.invoke(3)
// }
