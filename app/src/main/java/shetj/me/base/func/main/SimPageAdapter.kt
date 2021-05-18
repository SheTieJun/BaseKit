package shetj.me.base.func.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import me.shetj.base.saver.Saver
import me.shetj.base.view.edge.EdgeViewHolder
import shetj.me.base.R


class SimPageAdapter : PagingDataAdapter<Saver,EdgeViewHolder>(diffCallback) {

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

    override fun onBindViewHolder(holder: EdgeViewHolder, position: Int) {
        holder.setText(R.id.title, getItem(position)?.id.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EdgeViewHolder {
       val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
       return EdgeViewHolder(view)
    }

}