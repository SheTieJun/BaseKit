/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.base.view.edge

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.DIRECTION_BOTTOM
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.DIRECTION_RIGHT
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.DIRECTION_TOP
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
        if (direction == DIRECTION_BOTTOM || direction == DIRECTION_TOP) {
            recyclerView.addTranY(deltaDistance)
        }
    }

    private fun RecyclerView.addTranY(deltaDistance: Float) {
        val translationYDelta =
            level * recyclerView.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
        findEachViewHolder<EdgeViewHolder> {
            this?.let {
                this.itemView.translationY += translationYDelta
                animY.cancel()
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
        if (direction == DIRECTION_BOTTOM || direction == DIRECTION_TOP) {
            recyclerView.addEdgeAnim(20f.dp2px)
        }
    }

    override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
        if (direction == DIRECTION_BOTTOM || direction == DIRECTION_TOP) {
            recyclerView.addEdgeAnim(velocity)
        }
    }

    companion object {
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

        private const val FLING_TRANSLATION_MAGNITUDE = 0.5f
    }
}
