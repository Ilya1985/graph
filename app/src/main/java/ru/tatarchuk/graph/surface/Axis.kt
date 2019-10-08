package ru.tatarchuk.graph.surface

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.DecelerateInterpolator
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

/** Вся анимация во View зависить от двух значений : Scale по оси Х  и Scale по оси Y*/
/** Scale зависит от минимума и максимума значениея оси, поэтому при изменении этих значений */

class Axis {

    companion object {
        private val TAG = "<>${Axis::class.java.simpleName}"
    }

    private var animatorMax: ValueAnimator? = null
    private var animatorMin: ValueAnimator? = null

    var iInterpolator: TimeInterpolator = DecelerateInterpolator()

    var iDuration: Long = 500L

    var size: Float = 0f

    var scale = 1f

    /**Максимальное значение по оси*/
    var max: Float = 1f
        set(value) {
            animatorMax?.apply {
                cancel()
                removeAllUpdateListeners()
            }
            animatorMax = ValueAnimator.ofFloat(field, ceil(value)).apply {
                duration = iDuration
                interpolator = iInterpolator
                addUpdateListener {
                    field = it.animatedValue as Float
                    calculateScale()
                }
                start()
            }
            //animatorMax = ValueAnimator.ofFloat(field, ceil(value))
        }

    var min: Float = 0f
        set(value) {
            Log.i(TAG, "Value = ${DecimalFormat("0.00").format(value)}")
            animatorMin?.apply {
                cancel()
                removeAllUpdateListeners()
            }
            animatorMin = ValueAnimator.ofFloat(field, floor(value)).apply {
                duration = iDuration
                interpolator = iInterpolator
                addUpdateListener {
                    field = it.animatedValue as Float
                    calculateScale()
                }
                start()
            }
        }

    fun start() = min * scale

    fun end() = max * scale

    fun diff(): Float = max - min

    private fun calculateScale() {
        scale = size / (max - min)
    }
}