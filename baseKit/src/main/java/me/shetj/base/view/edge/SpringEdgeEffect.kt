package me.shetj.base.view.edge

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.*
import me.shetj.base.ktx.dp2px
import me.shetj.base.ktx.findEachViewHolder


class SpringEdgeEffect(private val recyclerView: RecyclerView, private val direction: Int) :
    AbEdgeEffect(recyclerView.context) {
    private val level = if (direction == DIRECTION_BOTTOM || direction == DIRECTION_RIGHT) {
        -1
    } else {
        1
    }

    override fun onPull(deltaDistance: Float) {
        super.onPull(deltaDistance)
    }

    override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance, displacement)
        if (direction == DIRECTION_BOTTOM||direction == DIRECTION_TOP) {
            recyclerView.addTranY(deltaDistance)
        }
    }


    private fun RecyclerView.addTranY(deltaDistance: Float) {
        val translationYDelta =
            level * recyclerView.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
        findEachViewHolder<EdgeViewHolder> {
            this?.let {
                this.itemView.translationY += translationYDelta
                animY?.cancel()
            }
        }
    }

    private fun RecyclerView.addEdgeAnim(velocity: Int) {
        val translationVelocity = level * velocity * FLING_TRANSLATION_MAGNITUDE
        findEachViewHolder<EdgeViewHolder> {
            this?.apply {
                animY.setStartVelocity(translationVelocity)
                    .start()
            }
        }
    }


    override fun onRelease() {
        super.onRelease()
        if (direction == DIRECTION_BOTTOM||direction == DIRECTION_TOP) {
            recyclerView.addEdgeAnim(20f.dp2px)
        }
    }

    override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
        if (direction == DIRECTION_BOTTOM||direction == DIRECTION_TOP) {
            recyclerView.addEdgeAnim(velocity)
        }
    }

    companion object {
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

        private const val FLING_TRANSLATION_MAGNITUDE = 0.5f
    }
}