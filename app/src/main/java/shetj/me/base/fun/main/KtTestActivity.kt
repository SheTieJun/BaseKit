package shetj.me.base.`fun`.main

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import me.shetj.base.base.BaseActivity
import me.shetj.base.kt.copy
import me.shetj.base.kt.runOnIo
import me.shetj.base.kt.toMessage
import me.shetj.base.tools.app.ArmsUtils.Companion.statuInScreen
import shetj.me.base.R
import timber.log.Timber
import java.util.concurrent.Executors
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class KtTestActivity : BaseActivity<MainPresenter>() {

    val scope =CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt_test)
        statuInScreen()
    }

    override fun initView() {
        //协程？默认后台？
//        在 GlobalScope 中启动的活动中的协程就像守护线程一样，不能使它们所在的进程保活。
        setTitle("KtTestActivity")
        val launch = GlobalScope.launch(Dispatchers.IO) {
            //在后台创建协程

            delay(1000)

        }

        launch.cancel()

        scope.launch {

        }

//        launch{
//                async {
//
//                }
//        }


        List(100) {
            launch {

            }
        }



        val async = GlobalScope.async {
            delay(100)

            "hahah"
        }



        print( async.isCompleted)

        //阻塞主线程
        runBlocking {
            delay(500)
        }


        launch {

        }
    }

    fun test() {
//        val async1 = async {
//
//        }
        runBlocking{

            launch { // 运行在父协程的上下文中，即 runBlocking 主协程
                println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.IO) { // 不受限的——将工作在主线程中
                println("IO            : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Unconfined) { // 不受限的——将工作在主线程中
                println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Default) { // 将会获取默认调度器
                println("Default               : I'm working in thread ${Thread.currentThread().name}")
            }
            launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) { // 将使它获得一个新的线程
                println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
            }


            //挂起函数
            withContext(Dispatchers.Main){

            }
            val launch = launch {

            }

            launch.cancelAndJoin() //取消该任务并且等待它结束
            print("")

            //创建一个新协程作用
            coroutineScope {
                launch {

                }

            }
            print("")


            measureNanoTime {

            }

            measureTimeMillis {

            }

            //异步
//            async1

            //使用一个可选的参数 start 并传值 CoroutineStart.LAZY，可以对 async 进行惰性操作。
            // 只有当结果需要被 await 或者如果一个 start 函数被调用，协程才会被启动
            val async = async(start = CoroutineStart.LAZY) {


            }
            async.start()
    //        async.await()

            print("")
        }
    }

    override fun initData() {
        "ss".copy(this) {
            return@copy
        }

        "ss".toMessage {
            return@toMessage
        }
        runOnIo{
            return@runOnIo
        }
    }




    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onActivityStart() {
        Timber.i("onActivityStart")

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onActivityStop() {
        Timber.i("onActivityStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onActivityResume() {
        Timber.i("onActivityResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onActivitypause() {
        Timber.i("onActivitypause")
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        Timber.i("onActivityCreate")
    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        Timber.i("onActivityDestroy")
    }

}
