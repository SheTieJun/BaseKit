package me.shetj.base.view.edge

import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.chad.library.adapter4.viewholder.QuickViewHolder

class EdgeViewHolder(view: View) : QuickViewHolder(view) {
    val animY: SpringAnimation = SpringAnimation(this.itemView, SpringAnimation.TRANSLATION_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )
}
