package shetj.me.base.func.md3

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import me.shetj.base.ktx.isTrue
import me.shetj.base.model.GrayThemeLiveData
import me.shetj.base.mvvm.databinding.BaseBindingFragment
import shetj.me.base.R
import shetj.me.base.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseBindingFragment<FragmentSecondBinding, Main2TestVM>(R.layout.fragment_second) {

    override fun setUpClicks() {
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        if (mViewModel.isGrayTheme) {
            binding.buttonChangeTheme.text = "灰色模式"
        }
    }

    override fun onInitialized() {
        super.onInitialized()
        binding.vm = mViewModel
    }
}