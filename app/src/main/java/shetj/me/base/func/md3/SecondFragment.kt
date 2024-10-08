package shetj.me.base.func.md3

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import me.shetj.base.mvvm.viewbind.BaseBindingFragment
import shetj.me.base.databinding.FragmentSecondBinding
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
        mBinding.wheelview.adapter =  object :Adapter(){
            override fun getItemCount(): Int {
                return 20
            }

            override fun getItem(position: Int): String {
              return "111-$position"
            }
        }

    }
}
