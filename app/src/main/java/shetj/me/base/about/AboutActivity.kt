package shetj.me.base.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.shetj.base.tools.app.ArmsUtils.Companion.statuInScreen
import shetj.me.base.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        statuInScreen(true)

    }
}