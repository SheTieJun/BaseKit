package shetj.me.base.func.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import shetj.me.base.R


class SimPageLoadAdapter(private val info:String) : LoadStateAdapter<BaseViewHolder>() {
    override fun onBindViewHolder(holder: BaseViewHolder, loadState: LoadState) {
        holder.setText(R.id.title, info+"xxxx")
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BaseViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        return BaseViewHolder(view)
    }

}