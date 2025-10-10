package me.shetj.base.base

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class TrackerBaseItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {


    //点击空白的地方时，会清空
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            val viewHolder = recyclerView.getChildViewHolder(view)
            if (viewHolder is TackerBaseViewHolder) {
                return viewHolder.getItemDetails()
            }
        }
        return null
    }
}

