package me.shetj.base.mvvm.databinding

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * The class used to provide spacing in recycler view,
 * @param spacing to provide space between list items.
 */
class RecyclerItemDecoration(spacing: Int, includeEdge: Boolean) : RecyclerView.ItemDecoration() {
    private var spacing = 0
    private var includeEdge = false
    private var displayMode = -1

    val HORIZONTAL = 0
    val VERTICAL = 1
    val GRID = 2
    val STAGGERED_GRID = 3

    init {
        this.spacing = spacing
        this.includeEdge = includeEdge
    }

    /**
     * the override method which used to manage item offsets,
     * here we are handling space between items.
     *
     * @param outRect the Rect object of cell item boundary.
     * @param view  the view of cell item.
     * @param parent  the recycler view.
     * @param state  the State object of recycler view which holds information of recycler view.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).bindingAdapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    /**
     * the method is used to handle spacing between list items,
     * which changes the rectangle offsets based on spacing where the rectangle is boundary of cell item.
     *
     * @param outRect the Rect object of cell item boundary.
     * @param layoutManager the layout manager of recycler view.
     * @param position the current index of item.
     * @param itemCount the number of list items.
     */
    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {
        // Resolve display mode automatically
        if (displayMode == -1) {
            displayMode = resolveDisplayMode(layoutManager)
        }
        when (displayMode) {
            HORIZONTAL -> {
                if (includeEdge) {
                    outRect.left = spacing
                    outRect.right = if (position == itemCount - 1) spacing else 0
                } else {
                    outRect.left = if (position == 0) 0 else spacing
                    outRect.right = if (position == itemCount - 1) spacing else 0
                }
                outRect.top = 0
                outRect.bottom = 0
            }
            VERTICAL -> {
                if (includeEdge) {
                    outRect.left = spacing
                    outRect.right = spacing
                    outRect.top = spacing
                    outRect.bottom = if (position == itemCount - 1) spacing else 0
                } else {
                    outRect.top = if (position == 0) 0 else spacing
                    outRect.bottom = 0
                }
            }
            GRID -> if (layoutManager is GridLayoutManager) {
                val spanCount = layoutManager.spanCount
                val column: Int = position % spanCount
                val rows = itemCount / spanCount

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount
                    outRect.right = (column + 1) * spacing / spanCount
                    outRect.top = spacing
                    outRect.bottom = if (position / spanCount == rows - 1) spacing else 0
                } else {
                    column * spacing / spanCount
                    outRect.left = column * spacing / spanCount
                    outRect.right = spacing - (column + 1) * spacing / spanCount
                    outRect.top = if (position / spanCount == 0) 0 else spacing
                    outRect.bottom = 0
                }
            }
            STAGGERED_GRID -> if (layoutManager is StaggeredGridLayoutManager) {
                val spanCount = layoutManager.spanCount
                val column: Int = position % spanCount
                val rows = itemCount / spanCount

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount
                    outRect.right = (column + 1) * spacing / spanCount
                    outRect.top = spacing
                    outRect.bottom = if (position / spanCount == rows - 1) spacing else 0
                } else {
                    column * spacing / spanCount
                    outRect.left = column * spacing / spanCount
                    outRect.right = spacing - (column + 1) * spacing / spanCount
                    outRect.top = if (position / spanCount == 0) 0 else spacing
                    outRect.bottom = 0
                }
            }
        }
    }

    /**
     * the method is used to get proper display mode of recycler view
     * @param layoutManager is the layout manager object which used to identify the orientation and recycler view type
     * @return the Int value of display type
     */
    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int {
        return when (layoutManager) {
            is GridLayoutManager -> GRID
            is StaggeredGridLayoutManager -> STAGGERED_GRID
            else -> {
                if (layoutManager?.canScrollHorizontally() == true) HORIZONTAL else VERTICAL
            }
        }
    }
}
