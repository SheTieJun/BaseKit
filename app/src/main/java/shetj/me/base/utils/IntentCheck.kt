package shetj.me.base.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import androidx.core.content.IntentSanitizer

object IntentCheck {

    fun checkIntent(activity: Activity,key:String){
        activity.apply {
            val forward = intent.getParcelableExtra(key,Intent::class.java)?:return
            val name: ComponentName = forward.resolveActivity(packageManager)
            if (name.packageName == activity.packageName && name.className == "safe_class") {
                // Redirect the nested intent.
                startActivity(forward)
            }
        }
    }

    fun checkIntent(intent:Intent ){
        val intent = IntentSanitizer.Builder()
            .allowComponentWithPackage("com.example.ActivityA")
            .allowDataWithAuthority("com.example")
            .allowType("text/plain")
            .build()
            .sanitizeByThrowing(intent)
    }
}