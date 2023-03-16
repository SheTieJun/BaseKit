

package shetj.me.base.func.md3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import me.shetj.base.ktx.isTrue
import me.shetj.base.model.GrayThemeLiveData
import shetj.me.base.R
import shetj.me.base.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {
    var isGrayTheme = false
    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        isGrayTheme = GrayThemeLiveData.getInstance().isTrue()
        if (isGrayTheme){
            binding.buttonChangeTheme.text = "灰色模式"
        }
        binding.buttonChangeTheme.setOnClickListener {
             isGrayTheme = !isGrayTheme
            GrayThemeLiveData.getInstance().postValue(isGrayTheme)
            if (isGrayTheme){
                binding.buttonChangeTheme.text = "正常模式"
            }else{
                binding.buttonChangeTheme.text = "灰色模式"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}