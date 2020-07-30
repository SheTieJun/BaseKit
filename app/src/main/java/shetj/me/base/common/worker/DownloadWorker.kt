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
import shetj.me.base.R


class DownloadWorker(context: Context, parameters: WorkerParameters) :
        CoroutineWorker(context, parameters) {

    private val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager

    override suspend fun doWork(): Result {
        val inputUrl = inputData.getString(KEY_INPUT_URL)
                ?: return Result.failure()
        val outputFile = inputData.getString(KEY_OUTPUT_FILE_NAME)
                ?: return Result.failure()
        // Mark the Worker as important
        val progress = "Starting Download"
        setForeground(createForegroundInfo(progress))
        download(inputUrl, outputFile)
        return Result.success()
    }

    private fun download(inputUrl: String, outputFile: String) {


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
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
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
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"

        fun startDownload(context: Context, inputUrl: String, outputFile: String){
            val inputData: Data = Data.Builder().apply {
                putString(KEY_INPUT_URL, inputUrl)
                putString(KEY_OUTPUT_FILE_NAME, outputFile)
            }.build()
            val request = OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(inputData).build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}