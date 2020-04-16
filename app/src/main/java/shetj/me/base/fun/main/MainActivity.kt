package shetj.me.base.`fun`.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.content_main.*
import me.shetj.base.base.BaseActivity
import me.shetj.base.base.TaskExecutor
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleCallBack
import me.shetj.base.tools.time.CodeUtil
import shetj.me.base.R
import timber.log.Timber

class MainActivity : BaseActivity<MainPresenter>(), View.OnClickListener {
    private var mBtnTest: Button? = null
    private var mTvTestCode: TextView? = null
    private var codeUtil: CodeUtil? = null
    private var viewpage2: ViewPager2? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPresenter = MainPresenter(this)
    }

    public override fun initView() {
        mBtnTest = findViewById<View>(R.id.btn_test) as Button
        mBtnTest!!.setOnClickListener(this)
        mTvTestCode = findViewById<View>(R.id.tv_test_code) as TextView
        mTvTestCode!!.setOnClickListener(this)
        viewpage2 = findViewById(R.id.viewPager2)
        viewpage2?.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewpage2?.adapter = AFragmentStateAdapter(this)
        viewpage2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
        btn_test_net.setOnClickListener {
            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
                    .apply {
                        isDefault = true
                    }
                    .execute(object : SimpleCallBack<String>(this) {
                        override fun onSuccess(data: String) {
                            super.onSuccess(data)

                        }

                        override fun onError(e: Exception) {
                            super.onError(e)
                            Timber.e(e)
                        }
                    })

        }
        findViewById<View>(R.id.fab).setOnClickListener { AppCompatDelegate.setDefaultNightMode(mPresenter!!.getNightModel()) }
//        testExecutor()
    }

    private fun testExecutor() {
        for (i in 0..100) {
            TaskExecutor.getInstance().executeOnDiskIO(Runnable {
                Thread.sleep(100)
                Timber.i("executeOnDiskIO:${Thread.currentThread().name}")
            })
        }
        for (i in 0..9) {
            TaskExecutor.getInstance().executeOnMainThread(Runnable {
                Timber.i("executeOnMainThread:${Thread.currentThread().name}")
            })
        }
    }

    public override fun initData() {
        codeUtil = CodeUtil(mTvTestCode!!)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_test -> startActivity(Intent(this, KtTestActivity::class.java))
            R.id.tv_test_code -> codeUtil!!.start()
            else -> {
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onActivityStart() {
        Timber.i("onActivityStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onActivityStop() {
        Timber.i("onActivityStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onActivityResume() {
        Timber.i("onActivityResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onActivityPause() {
        Timber.i("onActivityPause")
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        Timber.i("onActivityCreate")
    }

    override fun onActivityDestroy() {
        super.onActivityDestroy()
        codeUtil!!.stop()
        Timber.i("onActivityDestroy")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}