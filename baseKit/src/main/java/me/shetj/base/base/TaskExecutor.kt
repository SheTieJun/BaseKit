/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.base

import android.os.Handler
import android.os.Looper
import androidx.core.os.HandlerCompat
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import me.shetj.base.ktx.isMainThread

class TaskExecutor private constructor() {
    @Volatile
    private var mMainHandler: Handler? = null

    // 最大线程2，当不够时所有进入等待
    private val mDiskIO = ThreadPoolExecutor(
        2, 2,
        0L, TimeUnit.MILLISECONDS,
        SynchronousQueue(),
        object : ThreadFactory {
            private val THREAD_NAME_STEM = "base_thread_%d"
            private val mThreadId = AtomicInteger(0)
            override fun newThread(r: Runnable): Thread {
                val t = Thread(r)
                t.name = String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement())
                return t
            }
        }
    )

    fun executeOnDiskIO(runnable: Runnable) {
        mDiskIO.execute(runnable)
    }

    fun executeOnMainThread(runnable: Runnable) {
        if (isMainThread()) {
            runnable.run()
        } else {
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

    fun exit() {
        mDiskIO.shutdown() // Disable new tasks from being submitted
        try {
            // 等待 60 s
            if (!mDiskIO.awaitTermination(60, TimeUnit.SECONDS)) {
                // 调用 shutdownNow 取消正在执行的任务
                mDiskIO.shutdownNow()
                // 再次等待 60 s，如果还未结束，可以再次尝试，或则直接放弃
                if (!mDiskIO.awaitTermination(60, TimeUnit.SECONDS)) {
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

        fun exit() {
            getInstance().exit()
            sInstance = null
        }
    }
}
