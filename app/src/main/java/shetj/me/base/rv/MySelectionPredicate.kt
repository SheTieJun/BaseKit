package shetj.me.base.rv

import androidx.recyclerview.selection.SelectionTracker

class MySelectionPredicate : SelectionTracker.SelectionPredicate<Long>() {
    override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
        // 返回 true 表示可以设置条目的选择状态
        return true
    }

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
        // 返回 true 表示可以设置指定位置的条目的选择状态
        return true
    }

    override fun canSelectMultiple(): Boolean {
        // 返回 true 表示支持多选
        return true
    }
}