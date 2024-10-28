package shetj.me.base.rv

import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2024/10/21<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */
class TBaseViewHolder(view:View) :BaseViewHolder(view){
    private lateinit var itemDetails: ItemDetailsLookup.ItemDetails<Long>

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
        if (!::itemDetails.isInitialized) {
            itemDetails = object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int {
                    return bindingAdapterPosition
                }

                override fun getSelectionKey(): Long? {
                    return itemId
                }
            }
        }
        return itemDetails
    }
}