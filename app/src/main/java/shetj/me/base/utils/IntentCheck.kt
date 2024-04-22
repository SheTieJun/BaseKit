package shetj.me.base.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.IntentSanitizer

object IntentCheck {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkIntent(activity: Activity, key: String) {
        activity.apply {
            val forward = intent.getParcelableExtra(key, Intent::class.java) ?: return
            val name: ComponentName = forward.resolveActivity(packageManager)
            if (name.packageName == activity.packageName) {
                startActivity(forward)
            }
        }
    }

    fun checkIntent(intent: Intent): Intent {
        return IntentSanitizer.Builder()
            .allowComponentWithPackage("com.example.ActivityA")
            .allowDataWithAuthority("com.example")
            .allowType("text/plain")
            .build()
            .sanitizeByThrowing(intent)
    }
}