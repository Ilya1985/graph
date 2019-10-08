package ru.tatarchuk.graph.view.coordinates

import android.os.Handler
import android.util.Log

class Axiss(private val type: Type, private val callback: Callback) : Extreme.Callback {


    companion object {
        private val TAG = "<>${Axiss::class.java.simpleName}"
    }

    enum class Type {
        Abscissa, Ordinate
    }

    private val maximum = Extreme(Extreme.Type.MAX, this)

    private val minimum = Extreme(Extreme.Type.MIN, this)

    var startPoint = 0f
        set(value) {
            field = value
            Log.i(TAG, "new start point ${type.name} = $field")
            determineSize()
        }

    var endPoint = 0f
        set(value) {
            field = value
            Log.i(TAG, "new end point ${type.name} = $field")
            determineSize()
        }

    private var size = 0f
        set(value) {
            field = value
            determineScale()
        }

    private var scale = 1f
        set(value) {
            field = value
            callback.onScaleChange()
        }

    fun interval(): Float {
        val dif = getMax() - getMin()
        Log.i(TAG, "${type.name} diff = $dif")
        return when {
            dif > 0 && dif <= 3 -> 0.2f
            dif > 3 && dif <= 5 -> 0.5f
            dif > 5 && dif <= 8 -> 1f
            dif > 8 && dif <= 20 -> 2.5f
            dif > 20 && dif <= 50 -> 5f
            dif > 50 -> 20f
            else -> 0f
        }
    }

    val setMax: (Float) -> Unit = { v ->
        maximum.value = v
    }

    val setMin: (Float) -> Unit = { v ->
        minimum.value = v
    }

    val getMax: () -> Float = { maximum.value }

    val getMin: () -> Float = { minimum.value }

    val firstMarker: () -> Float = {
        /* if (getMin() % 1 == 0f){
             getMin()
         } else{*/
        getMin() + (interval() - (getMin() % interval()))
        //   }
    }

    private fun determineScale() {
        val diff = maximum.value - minimum.value
        scale = if (diff == 0f) 1f else size / diff
    }

    private fun determineSize() {
        size = endPoint - startPoint
        Log.i(TAG, "new size ${type.name} = $size")
        determineScale()
    }

    override fun extremesChanged() {
        determineScale()
    }

    interface Callback {
        fun onScaleChange()
    }

    private val convertToX: (Float) -> Float = { x -> startPoint + (x - getMin()) * scale }

    private val convertToY: (Float) -> Float = { y ->
        Log.i(TAG, "value = $y, min = ${getMin()}, scale = $scale, size = $size")
        endPoint - (y - getMin()) * scale
    }

    val getCoord: (Float) -> Float = { value -> if (type == Type.Abscissa) convertToX(value) else convertToY(value) }

    fun setAnimated(isOn: Boolean) {
        minimum.isAnimated = isOn
        maximum.isAnimated = isOn
    }
}