package ru.tatarchuk.graph.view.coordinates

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator

class Extreme(private val type: Type, private val callback: Callback) {

    companion object {
        private val TAG = "<>${Extreme::class.java.simpleName}"
    }

    var isAnimated = true

    var animator: ValueAnimator? = null

    var animationInterpolator: TimeInterpolator = DecelerateInterpolator()

    var animationDuration = 500L

    var value = 0f
        set(value) {

            if (field == value) return

            if (!isAnimated) {
                field = value
                callback.extremesChanged()
                return
            }

            animator?.apply {
                cancel()
                removeAllUpdateListeners()
            }

            animator = ValueAnimator.ofFloat(field, type.roundOff(value)).apply {
                duration = animationDuration
                interpolator = animationInterpolator
                addUpdateListener {
                    field = it.animatedValue as Float
                    callback.extremesChanged()
                }
                start()
            }
        }

    enum class Type(val roundOff: (Float) -> Float) {
        MIN({ value -> value - value % 0.5f/*floor(value)*/ }), MAX({ value -> value + value % 0.5f/*ceil(value)*/ });
    }

    interface Callback {
        fun extremesChanged()
    }
}