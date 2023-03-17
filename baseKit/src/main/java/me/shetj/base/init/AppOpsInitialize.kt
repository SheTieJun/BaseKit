package me.shetj.base.init

import android.app.AppOpsManager
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import me.shetj.base.ktx.logD

class AppOpsInitialize: ABBaseInitialize() {

    private val opsNotedForThisApp: MutableList<Pair<String, String>> = mutableListOf()

    private val onOpNotedCallback = @RequiresApi(VERSION_CODES.R)
    object : AppOpsManager.OnOpNotedCallback() {

        @Synchronized
        private fun saveAndLog(operation: String, stackTrace: String) {
            // Save
            opsNotedForThisApp.add(Pair(operation, stackTrace))
            operation.logD("AppOps")
            stackTrace.logD("AppOps")
        }

        /**
         * onNoted - Called when protected data is accessed via a synchronous call. For example,
         * onNoted would be triggered if an app requested the user's last known location and that
         * function returns the value synchronous (right away).
         */
        override fun onNoted(operation: SyncNotedAppOp) {
            val operationDescription =
               prettyOperationDescription("onNoted()", operation.op, operation.attributionTag)

            val prettyStackTrace =  prettyStackTrack(Thread.currentThread().stackTrace)

            saveAndLog(operationDescription, prettyStackTrace)
        }

        /**
         * onAsyncNoted - Called when protected data is accessed via an asynchronous callback. For
         * example, if an app subscribed to location changes, onAsyncNoted would be triggered when
         * the callback with a new location is called. A Geofence is another example.
         *
         * IMPORTANT NOTE: Because you are waiting on a GPS signal update, this might take a minute
         * or two to show up after you click the button.
         */
        override fun onAsyncNoted(asyncOp: AsyncNotedAppOp) {
            val operationDescription =
               prettyOperationDescription("onAsyncNoted()", asyncOp.op, asyncOp.attributionTag)

            // For an AsyncNotedAppOp, it's more effective to use the 'message' field instead of
            // retrieving the stack trace of the current thread to identify the call.
            val message = asyncOp.message

            saveAndLog(operationDescription, message)
        }

        /**
         *  onSelfNoted - Called when a developer calls {@link android.app.AppOpsManager#noteOp}
         *  to manually trigger a protected data access. This is the only callback that isn't
         *  triggered by the system. It's a way for apps to to blame themselves when they feel like
         *  they are accessing protected data and want to audit it.
         *
         *  It's a fairly uncommon use case, so in most cases, you won't need to do this.
         */
        override fun onSelfNoted(operation: SyncNotedAppOp) {
            val operationDescription =
                prettyOperationDescription("onSelfNoted()", operation.op, operation.attributionTag)
            val prettyStackTrace =  prettyStackTrack(Thread.currentThread().stackTrace)
            saveAndLog(operationDescription, prettyStackTrace)
        }
    }

    override fun initContent(context: Context) {
        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            val applicationOperationManager = context.getSystemService(AppOpsManager::class.java)
            applicationOperationManager?.setOnOpNotedCallback(context.mainExecutor, onOpNotedCallback)
        }
    }


    private fun prettyOperationDescription(
        methodName: String,
        operation: String,
        attributionTag: String?
    ) = "$methodName: $operation, Attribution Tag: ${attributionTag ?: "NONE"}"


    private fun prettyStackTrack(stackTraceElements: Array<StackTraceElement>): String {
        return stackTraceElements.joinToString("\n", "Stack Trace:\n", "\n") { "\t$it" }
    }

}