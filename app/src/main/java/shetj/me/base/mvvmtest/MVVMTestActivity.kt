package shetj.me.base.mvvmtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.rx3.asFlowable
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.loadImage
import me.shetj.base.ktx.logi
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.tools.file.FileQUtils.searchTypeFile
import org.koin.android.ext.android.get
import shetj.me.base.R
import shetj.me.base.bean.ResultMusic
import shetj.me.base.common.manager.TokenLoaderKT
import shetj.me.base.common.manager.TokenLoaderKT.Holder.instance
import shetj.me.base.common.manager.TokenManager
import shetj.me.base.databinding.ActivityMVVMTestBinding
import shetj.me.base.test.ProxyFactory
import shetj.me.base.test.TestProxy
import shetj.me.base.utils.TimeUtil
import timber.log.Timber
import kotlin.random.Random

class MVVMTestActivity : BaseBindingActivity<MVVMViewModel,ActivityMVVMTestBinding>() {

    private val click = View.OnClickListener {
        when (it?.id) {
            R.id.btn_change -> {
                //Observable双向更新
                mViewModel.change= "时间：${TimeUtil.getHMSTime()}"
                //LiveData
                mViewModel.timeLive.postValue(TimeUtil.getHMSTime())
            }
            R.id.btn_select_image -> {
                searchTypeFile(callback = {
                    it.let {
                        mViewModel.url.postValue(it.toString())
                    }
                })
            }
            R.id.testToken ->{
                repeat(10){i ->
                    launch {
                        instance.getToke()
                        TokenManager.getInstance().token = ""
                    }
                }
            }
        }
    }

    private val test = (0..10).random()
    /**
     * dl 这里用了单例
     */
    override fun initViewModel(): MVVMViewModel {
        return get()
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.btnChange.setOnClickListener(click)
        mViewBinding.btnSelectImage.setOnClickListener(click)
        //LiveData 的通知更新
        mViewModel.timeLive.observe(this, {
            Timber.tag("timeLive").i(it?.toString())
            mViewBinding.btnChange.text = it

        })
        mViewModel.timeLive.postValue(TimeUtil.getHMSTime())
        mViewModel.url.observe(this, {
           mViewBinding.image.loadImage(it)
        })
        mViewBinding.testToken.setOnClickListener(click)
        //用来测试是否时单例的viewModel
        Timber.tag("getViewModel").i("id = ${initViewModel()}")
        launch {
            listOf(1, 2, 3).asFlow().collect { Timber.i("asFlow() = $it") }
        }
        val channel = Channel<Int>()
        launch {
            // 这里可能是消耗大量 CPU 运算的异步逻辑，我们将仅仅做 5 次整数的平方并发送
            for (x in 1..5) channel.send(x * x)
            for (y in channel) {
                println(channel.receive())
            }
        }

        listOf(1, 2, 3).asFlow().asFlowable().subscribe {
            Timber.i("asFlow().asFlowable() = $it")
        }

        AnimationUtils.loadAnimation(this,R.anim.fade_ins).startOffset

    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val sb = StringBuilder()
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    when (permissions[i]) {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> sb.append("\n·  读写存储")
                        Manifest.permission.CAMERA -> sb.append("\n·  相机")
                        Manifest.permission.READ_PHONE_STATE -> sb.append("\n·  电话状态")
                        Manifest.permission.RECORD_AUDIO -> sb.append("\n·  麦克风录制")
                        Manifest.permission.WRITE_CALENDAR -> sb.append("\n·  添加日程")
                    }
                }
            }
        }
        if (sb.isNotEmpty()){
            //弹窗提示
        }
    }

}