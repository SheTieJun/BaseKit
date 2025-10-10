package shetj.me.base.func

import android.os.Bundle
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import me.shetj.base.ktx.createSelectTracker
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.mvvm.viewbind.BaseViewModel
import shetj.me.base.databinding.ActivitySelectTrackerTestBinding

/**
 * SelectTracker 功能测试界面
 * 用于验证 createSelectTracker 功能的各项特性
 */
class SelectTrackerTestActivity : BaseBindingActivity<ActivitySelectTrackerTestBinding, BaseViewModel>() {

    private lateinit var mAdapter: SelectTrackerTestAdapter
    private lateinit var selectionTracker: SelectionTracker<Long?>
    private var isMultiSelectMode = false
    private val testData = TestItem.createTestData().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupControlButtons()
        setupSelectionObserver()
    }

    /**
     * 设置工具栏
     */
    private fun setupToolbar() {
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "SelectTracker 功能测试"
        }
        mBinding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * 设置 RecyclerView
     */
    private fun setupRecyclerView() {
        mAdapter = SelectTrackerTestAdapter(testData, isMultiSelectMode)
        mBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SelectTrackerTestActivity)
            adapter = this@SelectTrackerTestActivity.mAdapter
            setHasFixedSize(true)
        }

        // 创建 SelectionTracker
        createSelectionTracker()
    }

    /**
     * 创建选择追踪器
     */
    private fun createSelectionTracker() {
        selectionTracker = mBinding.recyclerView.createSelectTracker(
            "select_tracker_test",
            isMultiSelectMode
        )
        mAdapter.setSelectionTracker(selectionTracker)
        mAdapter.setOnItemClickListener{ adapter, view, position ->
            val id = mAdapter.getItem(position).id

            if (selectionTracker.isSelected(id)) {
                // 如果已选中，则取消选择
                selectionTracker.deselect(id)
            } else {
                // 如果未选中，则选择
                selectionTracker.select(id)
            }
        }
    }

    /**
     * 设置控制按钮
     */
    private fun setupControlButtons() {
        // 模式切换按钮
        mBinding.btnToggleMode.setOnClickListener {
            toggleSelectionMode()
        }

        // 清除选择按钮
        mBinding.btnClearSelection.setOnClickListener {
            selectionTracker.clearSelection()
        }

        // 全选按钮（仅多选模式可用）
        mBinding.btnSelectAll.setOnClickListener {
            if (isMultiSelectMode) {
                testData.forEach { item ->
                    selectionTracker.select(item.id)
                }
            }
        }

        // 获取选中项按钮
        mBinding.btnGetSelected.setOnClickListener {
            showSelectedItems()
        }

        // 添加测试数据按钮
        mBinding.btnAddData.setOnClickListener {
            addTestData()
        }

        // 移除选中项按钮
        mBinding.btnRemoveSelected.setOnClickListener {
            removeSelectedItems()
        }

        updateModeButtonText()
        updateSelectAllButtonState()
    }

    /**
     * 设置选择状态观察者
     */
    private fun setupSelectionObserver() {
        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long?>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                updateSelectionInfo()
            }
        })
    }

    /**
     * 切换选择模式
     */
    private fun toggleSelectionMode() {
        isMultiSelectMode = !isMultiSelectMode
        
        // 清除当前选择
        selectionTracker.clearSelection()
        
        // 重新创建适配器和选择追踪器
        mAdapter = SelectTrackerTestAdapter(testData, isMultiSelectMode)
        mAdapter.setHasStableIds(true)
        mBinding.recyclerView.adapter = mAdapter
        
        createSelectionTracker()
        setupSelectionObserver()
        
        updateModeButtonText()
        updateSelectAllButtonState()
        updateSelectionInfo()
    }

    /**
     * 更新模式按钮文本
     */
    private fun updateModeButtonText() {
        mBinding.btnToggleMode.text = if (isMultiSelectMode) {
            "切换到单选模式"
        } else {
            "切换到多选模式"
        }
    }

    /**
     * 更新全选按钮状态
     */
    private fun updateSelectAllButtonState() {
        mBinding.btnSelectAll.isEnabled = isMultiSelectMode
        mBinding.btnSelectAll.alpha = if (isMultiSelectMode) 1.0f else 0.5f
    }

    /**
     * 更新选择信息显示
     */
    private fun updateSelectionInfo() {
        val selectionCount = selectionTracker.selection.size()
        val totalCount = testData.size
        val mode = if (isMultiSelectMode) "多选" else "单选"
        
        mBinding.tvSelectionInfo.text = "当前模式：$mode | 已选择：$selectionCount/$totalCount"
        
        // 更新选中项详情
        val selectedItems = mutableListOf<TestItem>()
        selectionTracker.selection.forEach { selectedId ->
            testData.find { it.id == selectedId }?.let { item ->
                selectedItems.add(item)
            }
        }
        
        if (selectedItems.isNotEmpty()) {
            val selectedTitles = selectedItems.joinToString(", ") { it.title }
            mBinding.tvSelectedItems.text = "选中项目：$selectedTitles"
        } else {
            mBinding.tvSelectedItems.text = "选中项目：无"
        }
    }

    /**
     * 显示选中项目详情
     */
    private fun showSelectedItems() {
        val selectedItems = mAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            mBinding.tvResult.text = "结果：未选择任何项目"
            return
        }
        
        val result = StringBuilder("结果：选中了 ${selectedItems.size} 个项目\n")
        selectedItems.forEachIndexed { index, item ->
            result.append("${index + 1}. ${item.title} (${item.type.displayName})\n")
        }
        
        mBinding.tvResult.text = result.toString()
    }

    /**
     * 添加测试数据
     */
    private fun addTestData() {
        val newId = (testData.maxOfOrNull { it.id } ?: 0) + 1
        val newItem = TestItem(
            id = newId,
            title = "新增项目 $newId",
            description = "这是动态添加的测试项目",
            type = TestItem.ItemType.values().random()
        )
        
        testData.add(newItem)
        mAdapter.notifyItemInserted(testData.size - 1)
        updateSelectionInfo()
    }

    /**
     * 移除选中的项目
     */
    private fun removeSelectedItems() {
        val selectedItems = mAdapter.getSelectedItems()
        if (selectedItems.isEmpty()) {
            mBinding.tvResult.text = "结果：没有选中的项目可以移除"
            return
        }
        
        // 清除选择状态
        selectionTracker.clearSelection()
        
        // 移除选中的项目
        selectedItems.forEach { item ->
            val index = testData.indexOf(item)
            if (index != -1) {
                testData.removeAt(index)
                mAdapter.notifyItemRemoved(index)
            }
        }
        
        // 刷新整个列表以确保位置正确
        mAdapter.notifyDataSetChanged()
        
        mBinding.tvResult.text = "结果：已移除 ${selectedItems.size} 个项目"
        updateSelectionInfo()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存选择状态
        if (::selectionTracker.isInitialized) {
            selectionTracker.onSaveInstanceState(outState)
        }
        outState.putBoolean("isMultiSelectMode", isMultiSelectMode)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 恢复选择状态
        isMultiSelectMode = savedInstanceState.getBoolean("isMultiSelectMode", false)
        if (::selectionTracker.isInitialized) {
            selectionTracker.onRestoreInstanceState(savedInstanceState)
        }
        updateModeButtonText()
        updateSelectAllButtonState()
        updateSelectionInfo()
    }
}