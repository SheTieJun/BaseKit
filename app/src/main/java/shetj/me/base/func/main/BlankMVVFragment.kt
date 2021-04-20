package shetj.me.base.func.main


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import me.shetj.base.mvvm.BaseBindingFragment
import shetj.me.base.databinding.FragmentBlankMvvmBinding
import shetj.me.base.mvvmtest.MVVMViewModel
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * 测试生命周期
 */
class BlankMVVMkFragment : BaseBindingFragment<MVVMViewModel,FragmentBlankMvvmBinding>() {
    private var cout: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("$cout onViewCreated\n ${mViewModel.test}")
    }

    companion object {
        fun newInstance(itemCount: Int): BlankMVVMkFragment =
                BlankMVVMkFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }

    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun initLazy() {
//        //start 只会执行一次
//        Log.i("Fragment$cout", "initLazy = ON_START")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    fun initLazy2() {
//        enabledOnBack = false
//        //不可见，
//        Log.i("Fragment$cout", "initLazy2 = ON_PAUSE")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun initLazy3() {
//        cout = requireArguments().getInt(ARG_ITEM_COUNT)
//        Log.i("Fragment$cout", "initLazy3 = ON_CREATE")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun initLazy4() {
//        Log.i("Fragment$cout", "initLazy4 = ON_DESTROY")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun initLazy5() {
//        //结束
//        Log.i("Fragment$cout", "initLazy5 = ON_STOP")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    fun initLazy6() {
//        enabledOnBack = true
//        //可见的时候
//        Log.i("Fragment$cout", "initLazy6 = ON_RESUME")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
//    fun initLazy7() {
//        //每次切换状态后，会置成这个歌
//        Log.i("Fragment$cout", "initLazy7 = ON_ANY")
//    }
//
//    override fun onBack() {
//        super.onBack()
//        Timber.i("Fragment$cout onBack = onBack:true")
//    }


//    开始onCreateView-> ON_CREATE -> onViewCreated-> ON_START -> ON_RESUME
//    结束前现会 ON_PAUSE -> ON_STOP -> ON_DESTROY
//    ON_RESUME -> ON_PAUSE
//    ON_PAUSE -> ON_RESUME

}
