package shetj.me.base.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import me.shetj.base.ktx.logE
import me.shetj.base.ktx.showToast
import java.util.concurrent.atomic.AtomicBoolean

class RVAdapterHelper {

    private val concatAdapter = ConcatAdapter() //合并的adapter
    private val headAdapters = mutableListOf<Adapter<*>>() //头部的adapter
    private val footAdapters = mutableListOf<Adapter<*>>() //尾部的adapter
    private var emptyAdapter: Adapter<*>? = null//空的adapter
    private var isShowHeadWithEmpty = false//是否显示空和头部
    private var isShowFootWithEmpty = false//是否显示空和尾部
    private val isEmpty  = AtomicBoolean(true)

    val adapter: ConcatAdapter
        get() = concatAdapter

    fun addHeader(adapter: Adapter<*>): RVAdapterHelper {
        val size = headAdapters.size
        headAdapters.add(adapter)
        concatAdapter.addAdapter(size, adapter)
        return this
    }

    fun addFooter(adapter: Adapter<*>): RVAdapterHelper {
        footAdapters.add(adapter)
        concatAdapter.addAdapter(adapter)
        return this
    }

    fun removeHeader(adapter: Adapter<*>): RVAdapterHelper {
        if (!headAdapters.contains(adapter)){
            "headAdapter中没有这个Adapter".logE("RVAdapterHelper")
            return this
        }
        headAdapters.remove(adapter)
        concatAdapter.removeAdapter(adapter)
        return this
    }

    fun removeAllHeader() {
        headAdapters.forEach {
            concatAdapter.removeAdapter(it)
        }
        headAdapters.clear()
    }

    fun removeFooter(adapter: Adapter<*>): RVAdapterHelper {
        if (footAdapters.contains(adapter)){
            footAdapters.remove(adapter)
            concatAdapter.removeAdapter(adapter)
        }else{
           "footAdapter中没有这个Adapter".logE("RVAdapterHelper")
        }
        return this
    }

    fun removeAllFooter() {
        footAdapters.forEach {
            concatAdapter.removeAdapter(it)
        }
        footAdapters.clear()
    }

    fun removeAdapter(adapter: Adapter<*>): RVAdapterHelper {
        if (headAdapters.contains(adapter)){
            removeHeader(adapter)
        }
        if (footAdapters.contains(adapter)){
            removeFooter(adapter)
        }
        concatAdapter.removeAdapter(adapter)
        checkEmpty()
        return this
    }

    fun addAdapter(adapter: Adapter<*>): RVAdapterHelper {
        val index = concatAdapter.adapters.size - footAdapters.size
        concatAdapter.addAdapter(index, adapter)
        checkEmpty()
        return this
    }

    private fun checkEmpty() {
        if (getItemCount() == 0){
            if (isEmpty.compareAndSet(false, true)) {
                if (!isShowFootWithEmpty){
                    footAdapters.forEach {
                        concatAdapter.removeAdapter(it)
                    }
                }
                if (!isShowHeadWithEmpty){
                    headAdapters.forEach {
                        concatAdapter.removeAdapter(it)
                    }
                }
                emptyAdapter?.let {
                    addAdapter(it)
                }
                return
            }
        }else{
            if (isEmpty.compareAndSet(true,false)) {
                headAdapters.forEach {
                    addHeader(it)
                }
                footAdapters.forEach {
                    addFooter(it)
                }
                emptyAdapter?.let {
                    removeAdapter(it)
                }
                return
            }
        }
    }

    fun addAdapter(index: Int, adapter: Adapter<*>): RVAdapterHelper {
        concatAdapter.addAdapter(headAdapters.size + index, adapter)
        return this
    }

    fun getItemCount(): Int {
        val headSum = headAdapters.sumOf { it.itemCount }
        val footSum = footAdapters.sumOf { it.itemCount }
        val empty = emptyAdapter?.itemCount ?: 0
        return concatAdapter.itemCount - headSum - footSum - empty
    }

    fun getItemViewType(position: Int): Int {
        return concatAdapter.getItemViewType(position)
    }

    fun getAllAdapters(): List<Adapter<*>> {
        return concatAdapter.adapters
    }

    fun getHeadAdapters(): List<Adapter<*>> {
        return headAdapters
    }

    fun getFootAdapters(): List<Adapter<*>> {
        return footAdapters
    }

    fun getAdapter(index: Int): Adapter<*> {
        return concatAdapter.adapters[headAdapters.size + index]
    }

    fun setEmptyAdapter(adapter: Adapter<*>) {
        emptyAdapter = adapter
        checkEmpty()
    }

}