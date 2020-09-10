package shetj.me.base.func.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.saver.Saver
import shetj.me.base.R


class SimPageAdapter : PagingDataAdapter<Saver,BaseViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Saver>() {

            override fun areItemsTheSame(oldItem: Saver, newItem: Saver): Boolean {
               return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Saver, newItem: Saver): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setText(R.id.title, getItem(position)?.id.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
       val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
       return BaseViewHolder(view)
    }

}