package ru.tatarchuk.graph.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import ru.tatarchuk.R
import ru.tatarchuk.graph.GraphElement
import ru.tatarchuk.graph.view.coordinates.CoordSys
import ru.tatarchuk.util.MetricsConverter

class CustomView : View, CoordSys.CoordSysCallback {

   /* companion object {
        private val TAG = "<>${CustomView::class.java.simpleName}"
        private const val DYNAMIC = "dynamic"
        private const val STATIC = "static"

        private const val FIRST_ZONE = 0.1f // Курс валют на выбранную дату 20dp
        private const val SECOND_ZONE = 0.6f // Динамическая система координат - основные данные 280dp
        private const val THIRD_ZONE = 0.1f // Диапазон дат для динамической систеиы 20dp
        private const val FOUR_ZONE = 0.1f   // Статическая система координат - данные за год. Это навигация по динамической системе - 40dp
        private const val FIFTH_ZONE = 0.1f  // Элементы управления системеой навигации 40dp
    }

    /**Данные*/
    private val currencies = Array(3) { createEmptyData() }

    /**Две систеиы координат, одна для иллюстпвции данных за весь год. Втора димамическая */
    private val dynamicSys = CoordSys(DYNAMIC, context).apply {
        callback = this@CustomView
        setAnimated(true)
    }

    private val staticSys = CoordSys(STATIC, context).apply {
        callback = this@CustomView
        setAnimated(false)
    }

    var dynamicStrokeWidth = MetricsConverter.dpToPx(1.5f, context)

    var staticStrokeWidth = MetricsConverter.dpToPx(1f, context)

    var cornerRadius = MetricsConverter.dpToPx(4f, context)

    var padding = MetricsConverter.dpToPx(4f, context)

    var staticBackground = RectF()

    var dynamicBackground = RectF()

    /**Colors*/
    private var colorShadow = resources.getColor(R.color.colorShadow)
    private var colorBackground = resources.getColor(android.R.color.background_light)

    /**Diagrams color*/
    var colors = intArrayOf(
        resources.getColor(R.color.colorGreen),
        resources.getColor(R.color.colorBlue),
        resources.getColor(R.color.colorPurple)
    )

    /**Dynamic system paintDynamic*/
    private val paintDynamic = Paint().apply {
        strokeWidth = dynamicStrokeWidth
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(dynamicStrokeWidth * 3)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            setShadowLayer(dynamicStrokeWidth * 5, dynamicStrokeWidth * 4f, dynamicStrokeWidth * 4f, colorShadow)
        }
    }
    /**Static system paintDynamic*/
    private val paintStatic = Paint().apply {
        strokeWidth = staticStrokeWidth
        style = Paint.Style.STROKE
    }

    private var staticBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        color = colorBackground
        isDither = true
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        setShadowLayer(staticStrokeWidth, 0f, 0f, colorShadow)
    }

    private val path = Path()*/

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

   /* override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        staticSys.changeSize(padding, width.toFloat() - padding, height * 0.8f, height * 0.9f)
        dynamicSys.changeSize(padding, width.toFloat() - padding, height * 0.1f, height * 0.7f)
        staticBackground = RectF(padding, height * 0.8f, width.toFloat() - padding, height * 0.9f)
        dynamicBackground = RectF(0f, 0f, width.toFloat(), height.toFloat())
        setExtremes()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawRoundRect(dynamicBackground, cornerRadius, cornerRadius, staticBackgroundPaint)
            dynamicSys.drawAxes(it)
            dynamicSys.drawDiagrams(it)
            it.drawRoundRect(staticBackground, cornerRadius, cornerRadius, staticBackgroundPaint)
            staticSys.drawDiagrams(it)
        }
    }*/

    override fun redraw(name: String) {
        invalidate()
    }

   /* private fun createEmptyData() = GraphElement("", "", mutableListOf())

    private fun CoordSys.drawDiagrams(canvas: Canvas) {
        for ((index, currency) in currencies.withIndex()) {
            currency.getPath(getCoord)
            canvas.drawPath(
                path,
                (if (name == DYNAMIC) paintDynamic else paintStatic).apply { color = colors[index] })
        }
    }

    fun setCurrency(currency: GraphElement, position: Int) {
        currencies[position].id = currency.id
        currencies[position].code = currency.code
        currencies[position].rates = currency.rates
        setExtremes()
    }

    fun delCurrency(position: Int) {
        currencies[position].clear()
        setExtremes()
    }

    /**Utils GraphElement*/
    private fun GraphElement.maxValue(): Float = rates.maxBy { it.second }?.second ?: 0f

    private fun GraphElement.minValue(): Float = rates.minBy { it.second }?.second ?: Float.MAX_VALUE

    private inline fun GraphElement.getPath(getCoordinates: (Int, Float) -> Pair<Float, Float>) {
        path.apply {
            reset()
            for ((index, element) in rates.withIndex()) {
                val coordinates = getCoordinates(index, element.second)
                Log.i(TAG, "next point {${coordinates.first}; ${coordinates.second}}")
                when (index) {
                    0 -> path.moveTo(coordinates.first, coordinates.second)
                    else -> path.lineTo(coordinates.first, coordinates.second)
                }
            }
        }
    }

    /**Calculate extremes*/
    private fun maxY(): Float = mutableListOf<Float>()
        .apply {
            currencies.forEach { add(it.maxValue()) }
        }
        .max() ?: 0f

    private fun minY(): Float {
        val result = mutableListOf<Float>()
            .apply {
                currencies.forEach { add(it.minValue()) }
            }
            .min() ?: 0f
        return if (result == Float.MAX_VALUE) 0f else result
    }

    private fun maxX(): Float = (mutableListOf<Int>()
        .apply {
            currencies.forEach { add(it.rates.size - 1) }
        }
        .max()?.toFloat() ?: 0f)

    private fun setExtremes() {
        val maxX = maxX()
        val minY = minY()
        val maxY = maxY()
        dynamicSys.setExtremes(0f, maxX, minY, maxY)
        staticSys.setExtremes(0f, maxX, minY, maxY)
    }*/
}