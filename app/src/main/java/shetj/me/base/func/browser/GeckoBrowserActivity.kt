package shetj.me.base.func.browser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView
import shetj.me.base.R

class GeckoBrowserActivity : AppCompatActivity() {

    // 推荐将 GeckoRuntime 放在 Application 级别作为单例以节省资源，这里为了演示放在 Activity 中
    private val geckoRuntime: GeckoRuntime by lazy {
        GeckoRuntime.create(this)
    }
    
    private lateinit var geckoSession: GeckoSession
    private lateinit var geckoView: GeckoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gecko_browser)

        geckoView = findViewById(R.id.geckoView)

        // 1. 创建一个浏览会话 (相当于新建一个 Tab)
        geckoSession = GeckoSession()

        // 2. 将引擎 (Runtime) 绑定到会话中
        geckoSession.open(geckoRuntime)

        // 3. 将会话绑定到 UI 控件上
        geckoView.setSession(geckoSession)

        // 4. 加载测试网页
        geckoSession.loadUri("https://html5test.com/")
    }
    
    override fun onDestroy() {
        // 离开页面时关闭会话
        geckoSession.close()
        super.onDestroy()
    }
}