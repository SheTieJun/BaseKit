package shetj.me.base.func

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import me.shetj.base.base.AbBaseActivity
import me.shetj.base.ktx.start
import shetj.me.base.func.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AbBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setOnExitAnimationListener { _ ->
            start<MainActivity>(isFinish = true)
        }
    }
}
