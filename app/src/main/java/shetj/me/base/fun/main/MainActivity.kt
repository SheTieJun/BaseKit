package shetj.me.base.`fun`.main

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.addListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import me.shetj.base.base.BaseActivity
import me.shetj.base.base.TaskExecutor
import me.shetj.base.ktx.toJson
import me.shetj.base.network.RxHttp
import me.shetj.base.network.callBack.SimpleNetCallBack
import me.shetj.base.tools.time.CodeUtil
import me.shetj.base.view.TipPopupWindow
import org.koin.androidx.scope.lifecycleScope
import shetj.me.base.R
import shetj.me.base.bean.ApiResult1
import shetj.me.base.bean.MusicBean
import timber.log.Timber

class MainActivity : BaseActivity<MainPresenter>(), View.OnClickListener {
    private var mBtnTest: Button? = null
    private var mTvTestCode: TextView? = null
    private var codeUtil: CodeUtil? = null
    private var viewpage2: ViewPager2? = null
    private val testPresenter:MainPresenter by lifecycleScope.inject()
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

        btn_test_tip.setOnClickListener {
            TipPopupWindow.showTipMsg(this, view = toolbar, tipMsg = "测试一下INFO")
        }

        tv_test_number.setOnClickListener { text ->

            val va = ValueAnimator.ofInt(0, 50).apply {
                duration = 300
                interpolator = DecelerateInterpolator()
                this.addUpdateListener {
                    (text as TextView).text = "X${(it.animatedValue as Int)}"
                }
                addListener(onStart = {
                    tv_test_number.animation =  AnimationUtils.loadAnimation(this@MainActivity,R.anim.zoom_in)?.apply {
                        interpolator = OvershootInterpolator()
                        repeatCount = -1
                        repeatMode = REVERSE
                    }
                },onEnd = {
                    tv_test_number.clearAnimation()
                })
            }
            va.start()

        }
        netTest()
        imgTest()
        findViewById<View>(R.id.fab).setOnClickListener {
            AppCompatDelegate.setDefaultNightMode( testPresenter.getNightModel())
        }
//        testExecutor()
    }

    private fun imgTest() {
//        iv_test.loadImage("https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png")
//        downloadImage(this,url ="https://staticqc.lycheer.net/account3/static/media/levelrule.45f3b2f1.png"){
//
//        }
    }

    private fun netTest() {
        btn_test_net.setOnClickListener {
            //            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
            //                    .executeCus(object : SimpleNetCallBack<ResultMusic>(this) {
            //                        override fun onSuccess(data: ResultMusic) {
            //                            super.onSuccess(data)
            //                            Timber.i(data.toJson())
            //                        }
            //
            //                        override fun onError(e: Exception) {
            //                            super.onError(e)
            //                            Timber.e(e)
            //                        }
            //                    })

            //            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
            //                    .executeCus(ResultMusic::class.java)
            //                    .map { it.data }
            //                    .subscribe ({
            //                        Timber.i(it.toJson())
            //                    },{
            //                        Timber.e(it)
            //                    })

            RxHttp.post("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
                    .executeCus(object : SimpleNetCallBack<ApiResult1<List<MusicBean>>>(this) {
                        override fun onSuccess(data: ApiResult1<List<MusicBean>>) {
                            super.onSuccess(data)
                            Timber.i(data.toJson())
                        }

                        override fun onError(e: Exception) {
                            super.onError(e)
                            Timber.e(e)
                        }
                    })


            //            RxHttp.get("https://ban-image-1253442168.cosgz.myqcloud.com/static/app_config/an_music.json")
            //                    .execute(object : SimpleNetCallBack<List<MusicBean>>(this) {
            //                        override fun onSuccess(data: List<MusicBean>) {
            //                            super.onSuccess(data)
            //                            Timber.i(data.toJson())
            //                        }
            //
            //                        override fun onError(e: Exception) {
            //                            super.onError(e)
            //                            Timber.e(e)
            //                        }
            //                    })

        }
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