

package me.shetj.base.mvvm.databinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.shetj.base.base.BaseControllerFunctionsImpl

/**
 * Base class for bottom sheet dialogs that using databind feature to bind the view
 * also Implements [BaseControllerFunctionsImpl] interface
 * @param T A class that extends [ViewDataBinding] that will be used by the dialog layout binding view.
 * @param layoutId the resource layout view going to bind with the [binding] variable
 */
abstract class BaseBindingBottomSheetDialogFragment<T : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    BottomSheetDialogFragment(), BaseControllerFunctionsImpl {
    lateinit var binding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = requireActivity()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
        setUpClicks()
        onInitialized()
    }
}