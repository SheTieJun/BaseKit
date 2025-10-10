package shetj.me.base.func

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import me.shetj.base.base.BaseSAdapter
import me.shetj.base.base.TackerBaseViewHolder
import me.shetj.base.ktx.logI
import shetj.me.base.R
import shetj.me.base.databinding.ItemSelectTrackerTestBinding

/**
 * SelectTracker 功能测试适配器
 * 继承自 BaseSAdapter，支持选择状态的视觉变化
 */
class SelectTrackerTestAdapter(
    data: MutableList<TestItem>? = null,
    private val isMultiSelect: Boolean = false
) : BaseSAdapter<TestItem, SelectTrackerTestAdapter.TestViewHolder>(R.layout.item_select_tracker_test, data) {

    init {
        setHasStableIds(true)
    }

    override val isMulti: Boolean = isMultiSelect

    override fun convert(holder: TestViewHolder, item: TestItem) {
        "convert def".logI("SelectTracker")
        holder.bind(item, getSelectTracker()?.isSelected(item.id) == true)
    }

    override fun convert(holder: TestViewHolder, item: TestItem, payloads: List<Any>) {
        super.convert(holder, item, payloads)
        "convert payloads ${holder.bindingAdapterPosition}".logI("SelectTracker")
        holder.bind(item, getSelectTracker()?.isSelected(item.id) == true)
    }



    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = ItemSelectTrackerTestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val holder = TestViewHolder(binding)
        return holder
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }

    /**
     * 获取当前选中的项目
     */
    fun getSelectedItems(): List<TestItem> {
        val selectedItems = mutableListOf<TestItem>()
        getSelectTracker()?.selection?.forEach { selectedId ->
            data.find { it.id == selectedId }?.let { item ->
                selectedItems.add(item)
            }
        }
        return selectedItems
    }





    /**
     * ViewHolder 类
     */
    class TestViewHolder(private val binding: ItemSelectTrackerTestBinding) : TackerBaseViewHolder(binding.root) {

          var item: TestItem ?=null

        fun bind(item: TestItem, isSelected: Boolean) {
            this.item = item
            binding.apply {
                // 设置基本信息
                tvTitle.text = item.title
                tvDescription.text = item.description
                tvType.text = item.type.displayName

                // 设置类型指示器颜色
                val typeColor = ContextCompat.getColor(root.context, item.type.colorRes)
                viewTypeIndicator.setBackgroundColor(typeColor)

                // 设置选中状态的视觉效果
                updateSelectionState(isSelected)
            }
        }

        private fun updateSelectionState(isSelected: Boolean) {
            binding.apply {
                if (isSelected) {
                    // 选中状态：改变背景色和透明度
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.design_default_color_primary_variant)
                    )
                    cardView.alpha = 0.8f
                } else {
                    // 未选中状态：恢复默认样式
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, android.R.color.white)
                    )
                    cardView.alpha = 1.0f
                }
            }
        }
    }
}