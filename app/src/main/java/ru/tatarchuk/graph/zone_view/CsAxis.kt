package ru.tatarchuk.graph.zone_view

import android.os.Handler

class CsAxis(private val type: Type) : CsExtreme.Callback {

    companion object {
        val TAG = "<>${CsAxis::class.java.simpleName}"
        private const val WAITING_TIME = 16L
    }

    private var isWaiting = false

    enum class Type {
        Abscissa, Ordinate
    }

    interface Callback {
        fun extremesChanged(type: Type)
    }

    var callback: Callback? = null

    override fun extremeChanged() {
        callback?.extremesChanged(type)
    }

    private val maximum = CsExtreme(CsExtreme.Type.MAX, this)

    private val minimum = CsExtreme(CsExtreme.Type.MIN, this)

    fun setExtremes(max: Float, min: Float) {
        maximum.setVal(max)
        minimum.setVal(min)
    }

    val getMin: () -> Float = { minimum.getValue() }

    val getMax: () -> Float = { maximum.getValue() }

    val diff: () -> Float = { maximum.getValue() - minimum.getValue() }

    /** При отрисовке шкалы на оси У - это значение(не координаты) первой метки
     * Т.е. это минимально значение в диапазоне У, кратное интрвалу(interval)
     * остальные метки вычисляются перебором значений с шагом interval */
    val firstMarker: () -> Float = { minimum.getValue() + (interval() - (minimum.getValue() % interval())) }

    fun interval(): Float {
        val dif = maximum.getValue() - minimum.getValue()
        return when {
            dif > 0 && dif <= 3 -> 0.2f
            dif > 3 && dif <= 5 -> 0.5f
            dif > 5 && dif <= 20 -> 1f
            //dif > 10 && dif <= 20 -> 2.5f
            dif > 20 && dif <= 50 -> 5f
            dif > 50 -> 20f
            else -> 0f
        }
    }

    fun setAnimated(isOn: Boolean) {
        minimum.isAnimated = isOn
        maximum.isAnimated = isOn
    }
}