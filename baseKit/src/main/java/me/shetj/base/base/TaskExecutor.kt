package me.shetj.base.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor


object TaskExecutor {
//    // 最大线程2，当不够时所有进入等待
//    private val mDiskIO = ThreadPoolExecutor(
//        2, 2,
//        10L, TimeUnit.MILLISECONDS,
//        SynchronousQueue(),
//        object : ThreadFactory {
//            private val THREAD_NAME_STEM = "base_thread_%d"
//            private val mThreadId = AtomicInteger(0)
//            override fun newThread(r: Runnable): Thread {
//                val t = Thread(r)
//                t.name = String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement())
//                return t
//            }
//        }
//    )

    @JvmStatic
    fun executeOnIO(runnable: Runnable) {
        Dispatchers.IO.asExecutor().execute(runnable)
    }

    @JvmStatic
    fun executeOnMain(runnable: Runnable) {
        Dispatchers.Main.asExecutor().execute(runnable)
    }

}
