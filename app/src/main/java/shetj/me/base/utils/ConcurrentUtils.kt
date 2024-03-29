package shetj.me.base.utils

import android.content.Context
import android.os.Process
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.concurrent.futures.CallbackToFutureAdapter.Completer
import androidx.concurrent.futures.await
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.runBlocking
import shetj.me.base.contentprovider.MediaItem
import shetj.me.base.contentprovider.fetchGalleryImages
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/9/19<br>
 */
object ConcurrentUtils {

    /**
     * Test android concurrent
     * 不要guava的多线程任务
     */
    fun testAndroidConcurrent() {
        val future = CallbackToFutureAdapter.getFuture { completer: Completer<String> ->
            // 任务开始 异步任务
            completer.set("任务完成")
            // completer.setException(RuntimeException("任务失败"))
            completer.addCancellationListener(
                Runnable {
                    // 任务取消
                },
                MoreExecutors.directExecutor()
            )
        }
        // 使用协程
        runBlocking {
            future.await()
        }
    }

    val executorService = newFixedThreadPool(5, "base", Process.THREAD_PRIORITY_BACKGROUND)

    fun newFixedThreadPool(
        nThreads: Int,
        poolName: String,
        linuxThreadPriority: Int
    ): ExecutorService {
        return Executors.newFixedThreadPool(
            nThreads,
            object : ThreadFactory {
                private val threadNum = AtomicInteger(0)
                override fun newThread(r: Runnable): Thread {
                    return object : Thread(poolName + threadNum.incrementAndGet()) {
                        override fun run() {
                            Process.setThreadPriority(linuxThreadPriority)
                            r.run()
                        }
                    }
                }
            }
        )
    }

    fun excu() {
        val anyFuture = executorService.submit(
            Runnable {
                // do something
            }
        )

        anyFuture.get()
    }

    fun test(context: Context) {
        val service: ListeningExecutorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10))
        val future: ListenableFuture<List<MediaItem>> =
            service.submit(Callable { fetchGalleryImages(context, "_data", true, 20, 0) })

        future.addListener(
            Runnable {
                // do something
            },
            MoreExecutors.directExecutor()
        )

        Futures.addCallback(
            future,
            object : FutureCallback<List<MediaItem>> {
                override fun onSuccess(result: List<MediaItem>?) {
                }

                override fun onFailure(t: Throwable) {
                }
            },
            MoreExecutors.directExecutor()
        )
    }
}
