package shetj.me.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Multi swipe refresh layout
 * 子布局触发刷新
 * @constructor Create empty Multi swipe refresh layout
 */
class MultiSwipeRefreshLayout : SwipeRefreshLayout {
    private var mSwipeChildren: Array<View?>? =null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    /**
     * Set the children which can trigger a refresh by swiping down when they are visible. These
     * views need to be a descendant of this view.
     * 子布局设置
     */
    fun setSwipeChildren(vararg ids: Int) {
        // Iterate through the ids and find the Views
        mSwipeChildren = arrayOfNulls(ids.size)
        for (i in ids.indices) {
            mSwipeChildren!![i] = findViewById(ids[i])
        }
    }
    /**
     * This method controls when the swipe-to-refresh gesture is triggered. By returning false here
     * we are signifying that the view is in a state where a refresh gesture can start.
     *
     * default, we need to manually iterate through our swipeable children to see if any are in a
     * state to trigger the gesture. If so we return false to start the gesture.
     */
    override fun canChildScrollUp(): Boolean {
        if (mSwipeChildren != null && mSwipeChildren!!.isNotEmpty()) {
            // Iterate through the scrollable children and check if any of them can not scroll up
            for (view in mSwipeChildren!!) {
                if (view != null && view.isShown && !view.canScrollVertically(-1)) {
                    // If the view is shown, and can not scroll upwards, return false and start the
                    // gesture.
                    return false
                }
            }
        }
        return true
    }
}