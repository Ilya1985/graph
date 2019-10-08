package ru.tatarchuk.graph.zone_view

import android.graphics.Path
import android.util.Log
import androidx.annotation.ColorRes
import ru.tatarchuk.R
import ru.tatarchuk.graph.zone_view.CsAxis.Type.Abscissa
import ru.tatarchuk.graph.zone_view.CsAxis.Type.Ordinate

open class Cs(val name: String) : Rectangle(), CsAxis.Callback {

    companion object {
        private val TAG = "<>${Cs::class.java.simpleName}"
    }

    interface Callback {
        fun scaleChanged(name: String)
    }

    enum class Colors(@ColorRes val color: Int) {
        GREEN(R.color.colorGreen),
        BLUE(R.color.colorBlue),
        PURPLE(R.color.colorPurple),
        ORANGE(R.color.colorOrange),
        GRAY(R.color.colorGray),
        YELLOW(R.color.colorYellow)
    }

    var callback: Callback? = null

    /**Данные, которые отображаются в системе координат(максимум 6, цвет является ключом)
     *При добавлении данных инициализируется Path. Таки мобразом при отрисовке диаграмм не происходит выделения памяти*/
    private val rates = mutableMapOf<Colors, Pair<MutableList<Float>, Path>>()

    fun getRates() = rates

    fun putRate(color: Colors, rate: MutableList<Float>) {
        rates[color] = Pair(rate, Path())
        determineExtremes()
    }

    fun delRate(color: Colors) {
        rates.remove(color)
        determineExtremes()
    }

    private val abscissa = CsAxis(Abscissa).apply { callback = this@Cs }

    private val ordinate = CsAxis(Ordinate).apply { callback = this@Cs }

    override fun extremesChanged(type: CsAxis.Type) {
        when (type) {
            Abscissa -> determineScaleX()
            Ordinate -> determineScaleY()
        }
    }

    private var scaleX = 0f
        set(value) {
            field = value
            createDiagrams()
            callback?.scaleChanged(name)
        }

    private var scaleY = 0f
        set(value) {
            field = value
            createDiagrams()
            callback?.scaleChanged(name)
        }

    override fun setSize(left: Float, right: Float, top: Float, bottom: Float) {
        super.setSize(left, right, top, bottom)
        determineScaleX()
        determineScaleY()
    }

    fun setExtremes(maxX: Float, minX: Float, maxY: Float, minY: Float) {
        abscissa.setExtremes(maxX, minX)
        ordinate.setExtremes(maxY, minY)
    }

    val coordX: (Float) -> Float = { x -> left() + (x - abscissa.getMin()) * scaleX }

    val coordY: (Float) -> Float = { y -> bottom() - (y - ordinate.getMin()) * scaleY }

    private val getCoords: (Int, Float) -> Pair<Float, Float> = { x, y -> Pair(coordX(x.toFloat()), coordY(y)) }

    private fun intervalX() = abscissa.interval()

    private fun intervalY() = ordinate.interval()

    //fun getFirstMarkerY() = ordinate.firstMarker()

    /***/
    fun setAnimated(isOn: Boolean) {
        abscissa.setAnimated(isOn)
        ordinate.setAnimated(isOn)
    }

    private val maxX: () -> Float = { abscissa.getMax() }

    private val minX: () -> Float = { abscissa.getMin() }

    private val maxY: () -> Float = { ordinate.getMax() }

    private val minY: () -> Float = { ordinate.getMin() }

    fun getTagsOnTheScale(): MutableList<Pair<Float, Float>> {
        val result = mutableListOf<Pair<Float, Float>>()
        var y = ordinate.firstMarker()
        while (y < maxY()) {
            result.add(Pair(y, coordY(y)))
            y += intervalY()
        }
        return result
    }

    private fun determineScaleX() {
        val diff = abscissa.diff()
        scaleX = if (diff == 0f) 1f else width() / diff
    }

    private fun determineScaleY() {
        val diff = ordinate.diff()
        scaleY = if (diff == 0f) 1f else height() / diff
    }

    private fun determineExtremesY() {
        if (rates.isEmpty()) {
            ordinate.setExtremes(0f, 0f)
            return
        }
        var maximum = 0f
        var minimum = Float.MAX_VALUE
        rates.forEach { r ->
            r.value.first.max()?.let { if (maximum < it) maximum = it }
            r.value.first.min()?.let { if (minimum > it) minimum = it }
        }
        ordinate.setExtremes(maximum, minimum)
    }

    private fun determineExtremesX() {
        var m = 0
        for (r in rates) {
            if (r.value.first.size > m) m = r.value.first.size
        }
        abscissa.setExtremes(m.toFloat() - 1, 0f)
    }

    private fun determineExtremes() {
        determineExtremesY()
        determineExtremesX()
    }

    fun createDiagrams() {
        val start = System.currentTimeMillis()
        for (rate in rates.values) {
            rate.apply {
                second.reset()
                for ((i, f) in first.withIndex()) {
                    val x = coordX(i.toFloat())
                    val y = coordY(f)
                    when (i) {
                        0 -> second.moveTo(x, y)
                        else -> second.lineTo(x, y)
                    }
                }
            }
        }
        Log.i(TAG, "$name createDiagrams $name time = ${System.currentTimeMillis() - start}")
    }

}