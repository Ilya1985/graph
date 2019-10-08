package ru.tatarchuk.graph.zone_view

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator

class CsExtreme(private val type: Type, private val callback: Callback) :

    ValueAnimator.AnimatorUpdateListener {

    var time = 0L

    override fun onAnimationUpdate(p0: ValueAnimator?) {
        p0?.let {
            Log.i(TAG, "update time ${System.currentTimeMillis() - time}")
            value = it.animatedValue as Float
        }
    }

    companion object {
        private val TAG = "<>${CsExtreme::class.java.simpleName}"
    }

    var isAnimated = true

    var animationInterpolator: TimeInterpolator = DecelerateInterpolator()

    var animationDuration = 300L

    var animator: ValueAnimator = ValueAnimator.ofFloat(0f).apply {
        duration = animationDuration
        interpolator = animationInterpolator
        addUpdateListener(this@CsExtreme)
    }

    private var value = 0f
        set(value) {
            field = value
            time = System.currentTimeMillis()
            callback.extremeChanged()
        }

    fun getValue() = value

    fun setVal(v: Float) {
        if (v == value) return

        if (!isAnimated) {
            value = v
            return
        }

        animator.apply {
            cancel()
            setFloatValues(value, type.roundOff(v))
            time = System.currentTimeMillis()
            start()
        }
    }

    enum class Type(val roundOff: (Float) -> Float) {
        MIN({ value -> value - value % 0.5f/*floor(value)*/ }), MAX({ value -> value + value % 0.5f/*ceil(value)*/ });
    }

    interface Callback {
        fun extremeChanged()
    }
}