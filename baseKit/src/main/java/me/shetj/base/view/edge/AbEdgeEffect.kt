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

import android.content.Context
import android.graphics.BlendMode
import android.graphics.Canvas
import android.widget.EdgeEffect

/**
 * like overScroll
 *
 * 构造方法。在这个方法里会初始化阴影的颜色。阴影颜色默认为0.25透明度的主题颜色。所以如果想要修改边缘阴影的颜色，
 * 可以修改app或者页面theme的colorPrimary。
 */
abstract class AbEdgeEffect(context: Context) : EdgeEffect(context) {

    override fun onPull(deltaDistance: Float) {
        super.onPull(deltaDistance)
    }

    /**
     * 设置滑动拉出阴影时的拉伸距离和手指位置。
     * @param deltaDistance 阴影的拉伸距离，值为0-1，它决定了阴影的大小。
     * @param displacement 触摸点的位置，值为0-1，它会影响阴影的曲线效果。
     */
    override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance, displacement)
    }

    /**
     * 对象释放。调用这个方法后，阴影会有一个衰减到消失的过程。
     */
    override fun onRelease() {
        super.onRelease()
    }

    /**
     * 通过滑动速度设置阴影的显示效果。一般在布局快速滑动(fling)到边界时，通过剩余的滑动速度显示阴影。
     */
    override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
    }

    /**
     * 设置阴影颜色。这个方法一般很少使用，不过有一些布局会提供单独的边缘阴影颜色设置的方法，就是间接调用了这个方法。
     */
    override fun setColor(color: Int) {
        super.setColor(color)
    }

    override fun setBlendMode(blendmode: BlendMode?) {
        super.setBlendMode(blendmode)
    }

    override fun getColor(): Int {
        return super.getColor()
    }

    override fun getBlendMode(): BlendMode? {
        return super.getBlendMode()
    }

    /**
     * 绘制阴影。这是核心方法，用于绘制阴影效果，在view的draw方法中调用。
     * @return 如果返回true，表示阴影动画还没结束，应该在下一帧继续绘制。
     */
    override fun draw(canvas: Canvas?): Boolean {
        return super.draw(canvas)
    }

    override fun getMaxHeight(): Int {
        return super.getMaxHeight()
    }

    /**
     * 判断阴影是否绘制完成。边缘阴影的显示效果是一个动画的过程，所以一次阴影的显示是又多次draw绘制完成的。
     */
    override fun isFinished(): Boolean {
        return super.isFinished()
    }

    override fun finish() {
        super.finish()
    }

    /**
     * 设置宽高。这里的宽高是布局内容显示区域的宽高，即布局的宽高减去padding。
     */
    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
    }
}
