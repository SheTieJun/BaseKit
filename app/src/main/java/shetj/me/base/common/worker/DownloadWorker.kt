package shetj.me.base.common.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import me.shetj.base.ktx.logi
import me.shetj.base.network_coroutine.KCHttp
import shetj.me.base.R


/**
 * 测试下载
 */
class DownloadWorker(context: Context, parameters: WorkerParameters) :
        CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val inputUrl = inputData.getString(KEY_INPUT_URL)
                ?: return Result.failure()
        val outputUrl = inputData.getString(KEY_OUT_PUT_URL)
                ?: return Result.failure()
        val filename = inputData.getString(KEY_OUTPUT_FILE_NAME)
                ?: return Result.failure()
        // Mark the Worker as important
        val progress = "Starting Download"
        setForeground(createForegroundInfo(progress))
        download(inputUrl, outputUrl, filename)
        return Result.success()
    }

    private suspend fun download(inputUrl: String, outputFile: String, fileName: String) {
//        repeat(100){
//            setForeground(createForegroundInfo("${it}%"))
//            delay(500)
//        }
//        setForeground(createForegroundInfo("download ok"))

        KCHttp.download(inputUrl, "$outputFile/$fileName", process = { _, _, process ->
            setForeground(createForegroundInfo("${(process * 100).toInt()}%"))
        }, success = {
            it.absolutePath.logi()
            setForeground(createForegroundInfo("download ok"))
        },error = {
            it.message.logi()
        })
    }


    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {

        //cancel
        val intent = WorkManager.getInstance(applicationContext)
                .createCancelPendingIntent(id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, getChannelID())
                .setContentTitle(getTitle())
                .setTicker(getTitle())
                .setContentText(progress)
                .setSmallIcon(R.mipmap.shetj_logo)
                .setOngoing(true) //防止滑动删除
                .addAction(R.drawable.picture_icon_delete, "取消", intent)
                .build()

        return ForegroundInfo("下载文件".hashCode(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (NotificationManagerCompat.from(applicationContext).getNotificationChannel(
                        getChannelID()
                ) == null) {
            val name = "文件下载"
            val description = "文件下载"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(getChannelID(), name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            return NotificationManagerCompat.from(applicationContext).createNotificationChannel(mChannel)
        }
    }

    private fun getTitle(): String {
        return "文件下载"
    }

    private fun getChannelID(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            "文件下载"
        } else {
            ""
        }
    }

    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUT_PUT_URL = "KEY_OUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"

        fun startDownload(context: Context, inputUrl: String, outputFile: String, fileName: String) {
            val inputData: Data = Data.Builder().apply {
                putString(KEY_INPUT_URL, inputUrl)
                putString(KEY_OUTPUT_FILE_NAME, fileName)
                putString(KEY_OUT_PUT_URL, outputFile)
            }.build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(inputData).build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}