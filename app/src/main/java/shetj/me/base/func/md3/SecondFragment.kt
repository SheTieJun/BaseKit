package shetj.me.base.func.md3

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import me.shetj.base.mvvm.viewbind.BaseBindingFragment
import shetj.me.base.R
import shetj.me.base.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseBindingFragment<FragmentSecondBinding, Main2TestVM>() {

    override fun setUpClicks() {
        mBinding.buttonSecond.setOnClickListener {
            findNavController().popBackStack()
        }
        if (mViewModel.isGrayTheme) {
            mBinding.buttonChangeTheme.text = "灰色模式"
        }
    }

    override fun onInitialized() {
        super.onInitialized()
        mBinding.vm = mViewModel
    }
}