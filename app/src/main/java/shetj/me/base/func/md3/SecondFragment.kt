package shetj.me.base.func.md3

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import me.shetj.base.ktx.launch
import me.shetj.base.mvvm.viewbind.BaseBindingFragment
import shetj.me.base.databinding.FragmentSecondBinding
import shetj.me.base.view.LrcEntry
import shetj.me.base.view.LrcView
import shetj.me.base.view.LrcView.OnPlayClickListener
import shetj.me.base.wheel.WheelView.Adapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseBindingFragment<FragmentSecondBinding, Main2TestVM>() {

    @SuppressLint("SetTextI18n")
    override fun setUpClicks() {
        mBinding.buttonSecond.setOnClickListener {
            findNavController().popBackStack()
        }
        if (mViewModel.isGrayTheme) {
            mBinding.buttonChangeTheme.text = "灰色模式"
        }
    }

    override fun onBack() {
        enabledOnBack = false
        findNavController().popBackStack()
    }

    override fun onInitialized() {
        super.onInitialized()
        enabledOnBack = true
        mBinding.vm = mViewModel
    }

    override fun initBaseView() {
        super.initBaseView()
        val lrcEntries = mutableListOf<LrcEntry>().apply {

            add(
                LrcEntry(
                    5000L, "心之所向，即是远方，\n" +
                            "一个人至少拥有一个梦想，\n" +
                            "有一个理由去坚强，\n" +
                            "心若没有栖息的地方，"
                )
            )
            add(
                LrcEntry(
                    5000, "到哪里，都是在流浪，\n" +
                            "人生最大的遗憾，"
                )
            )

            add(
                LrcEntry(
                    10000, "不是你不可以\n" +
                            "而是你不曾为了自己想要的生活\n" +
                            "去做出努力和争取\n" +
                            "努力让自己少点遗憾吧"
                )
            )

            add(
                LrcEntry(
                    15000, "什么时候开始都不算晚\n" +
                            "愿你心有所向，行有所达\n" +
                            "心之所向，即是远方"
                )
            )
        }

        mBinding.lrcView.loadLrc(lrcEntries)
        mBinding.lrcView.setDraggable(true) { view, time -> false }


        launch {
            repeat(16){
                delay(1000)
                mBinding.lrcView.updateTime(it*1000L)
            }
        }

    }
}
