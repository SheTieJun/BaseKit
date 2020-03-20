package me.shetj.base.base

import io.reactivex.android.schedulers.AndroidSchedulers
import me.shetj.base.kt.isMainThread
import java.util.concurrent.Executors

class TaskExecutor private constructor() {

    private val mDiskIO = Executors.newFixedThreadPool(3) { r -> Thread(r) }

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

        val instance: TaskExecutor
            get() {
                return sInstance?: synchronized(TaskExecutor::class.java) {
                    return TaskExecutor().also {
                        sInstance = it
                    }
                }
            }
    }
}