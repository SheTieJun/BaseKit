

package me.shetj.base.ktx

import me.shetj.base.base.TaskExecutor

fun runOnMain(run: Runnable) {
    TaskExecutor.executeOnMain(run)
}

fun runOnIo(run: Runnable) {
    TaskExecutor.executeOnIO (run)
}
