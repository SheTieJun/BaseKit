package me.shetj.base.base

import io.reactivex.android.schedulers.AndroidSchedulers
import me.shetj.base.kt.isMainThread
import java.util.concurrent.Executors

class TaskExecutor private constructor() {

    //最大线程2，当不够时所有进入等待
    private val mDiskIO = Executors.newFixedThreadPool(2) { r -> Thread(r) }

    fun executeOnDiskIO(runnable: Runnable) {
        mDiskIO.execute(runnable)
    }

    fun executeOnMainThread(runnable: Runnable) {
        if (isMainThread()) {
            runnable.run()
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(runnable)
        }
    }

    companion object {

        @Volatile
        private var sInstance: TaskExecutor? = null

        fun getInstance(): TaskExecutor {
            return sInstance?: synchronized(TaskExecutor::class.java) {
                return TaskExecutor().also {
                    sInstance = it
                }
            }
        }
    }
}