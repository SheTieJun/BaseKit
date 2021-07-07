package me.shetj.base.base

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import me.shetj.base.ktx.isMainThread
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class TaskExecutor private constructor() {
    @Volatile
    private var mMainHandler: Handler? = null

    //最大线程2，当不够时所有进入等待
    private val mDiskIO = Executors.newFixedThreadPool(2, object : ThreadFactory {
        private val THREAD_NAME_STEM = "base_thread_%d"
        private val mThreadId = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            val t = Thread(r)
            t.name = String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement())
            return t
        }
    })

    fun executeOnDiskIO(runnable: Runnable) {
        mDiskIO.execute(runnable)
    }


    fun executeOnMainThread(runnable: Runnable) {
        if (isMainThread()) {
            runnable.run()
        }else{
            if (mMainHandler == null) {
                synchronized(TaskExecutor::class.java) {
                    if (mMainHandler == null) {
                        mMainHandler =
                            HandlerCompat.createAsync(Looper.getMainLooper())
                    }
                }
            }
            mMainHandler!!.post(runnable)
        }
    }

    fun exit(){
        mDiskIO.shutdown() // Disable new tasks from being submitted
        try {
            // 等待 60 s
            if (!mDiskIO.awaitTermination(60, TimeUnit.SECONDS)) {
                // 调用 shutdownNow 取消正在执行的任务
                mDiskIO.shutdownNow()
                // 再次等待 60 s，如果还未结束，可以再次尝试，或则直接放弃
                if (!mDiskIO.awaitTermination(60, TimeUnit.SECONDS)){
                    System.err.println("线程池任务未正常执行结束")
                }
            }
        } catch (ie: InterruptedException) {
            // 重新调用 shutdownNow
            mDiskIO.shutdownNow()
        }
    }

    companion object {

        @Volatile
        private var sInstance: TaskExecutor? = null

        fun getInstance(): TaskExecutor {
            return sInstance ?: synchronized(TaskExecutor::class.java) {
                return TaskExecutor().also {
                    sInstance = it
                }
            }
        }

        @JvmStatic
        fun executeOnIO(runnable: Runnable) {
            getInstance().executeOnDiskIO(runnable)
        }

        @JvmStatic
        fun executeOnMain(runnable: Runnable) {
            getInstance().executeOnMainThread(runnable)
        }

        fun exit(){
            getInstance().exit()
            sInstance = null
        }
    }
}