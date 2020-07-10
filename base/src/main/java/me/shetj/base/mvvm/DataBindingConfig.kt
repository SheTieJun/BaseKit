package me.shetj.base.mvvm

import android.util.SparseArray
import androidx.lifecycle.ViewModel

class DataBindingConfig(val layout: Int, val vmVariableId: Int, val stateViewModel: ViewModel) {
    private val bindingParams: SparseArray<Any> = SparseArray()

    fun getBindingParams(): SparseArray<Any> {
        return bindingParams
    }

    fun addBindingParam(variableId: Int, `object`: Any?): DataBindingConfig {
        if (bindingParams.get(variableId) == null) {
            bindingParams.put(variableId, `object`)
        }
        return this
    }

}