package shetj.me.base.func.md3

import android.graphics.drawable.AnimatedStateListDrawable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.lifecycle.withResumed
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logI
import me.shetj.base.tools.app.LanguageKit
import shetj.me.base.R
import shetj.me.base.databinding.FragmentFirstBinding
import shetj.me.base.day.Month
import shetj.me.base.day.MonthAdapter
import shetj.me.base.utils.AudioManageX
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var isChecked = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonChange.setOnClickListener {
            if (!isEn) {
                LanguageKit.changeLanguage(requireContext(), Locale.ENGLISH)
            } else {
                LanguageKit.changeLanguage(requireContext(), Locale.CHINA)
            }
        }
        binding.arcProgressView.value = 50f

        launch {
            lifecycle.withResumed {
                binding.textviewFirst.text = AudioManageX(requireActivity()).checkDevice()
            }
        }
        addMonthAdapter()

        binding.img.let  {imageView ->
            imageView.setImageResource(R.drawable.select_anim)
            // 设置点击监听
            imageView.setOnClickListener {
//                isChecked = !isChecked
//                imageView.isActivated = isChecked
                (imageView.drawable as AnimatedVectorDrawable).start()
//                // 延迟重置状态
//                Handler(Looper.getMainLooper()).postDelayed({
//                    isChecked = !isChecked
//                    imageView.isActivated = isChecked
//                }, 3000) // 3秒后切换回原始状态
            }
        }
    }

    private fun addMonthAdapter() {
        val current = Month.current()

        """daysInMonth:${current.daysInMonth}
            |month:${current.month}
            |year:${current.year}
            |timeInMillis:${current.timeInMillis}
            |daysInWeek:${current.daysInWeek}
        """.trimMargin().logI()
        binding.groupLayout.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.groupLayout.adapter = MonthAdapter(current)
    }

    val isEn: Boolean
        get() {
            return LanguageKit.getAppLocale(requireContext()).let {
                it.country == Locale.ENGLISH.country && it.language == Locale.ENGLISH.language
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
