package shetj.me.base.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.shetj.base.tools.app.ArmsUtils.Companion.statuInScreen
import shetj.me.base.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        statuInScreen(true)
        //logo
        //关于
        //版本更新
        //github
        //联系方式
        //项目地址
    }
}