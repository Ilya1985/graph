package ru.tatarchuk.graph.zone_view

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import org.threeten.bp.LocalDate
import ru.tatarchuk.R
import ru.tatarchuk.graph.GraphElement
import ru.tatarchuk.util.AppNumFormatter
import ru.tatarchuk.util.MetricsConverter

class Graph : View, Cs.Callback {

    companion object {
        private val TAG = "<>${Graph::class.java.simpleName}"
        private const val STATIC_NAME: String = "static"
        private const val SIZE = 2
        private const val DYNAMIC_NAME = "dynamic"
        private const val WAITING_TIME = 16L
    }

    private var isWait = false

    override fun scaleChanged(name: String) {
        when (name) {
            STATIC_NAME -> setDynamicsDiagrams()
            DYNAMIC_NAME -> {
                invalidate()
                /*if (!isWait) {
                    isWait = true
                    Handler().postDelayed({
                        invalidate()
                    }, WAITING_TIME)
                }*/
            }
        }
    }

    private val selectDateData = DateRange()

    /**Двесистемы координат
     * 1-я */
    private val dynamicCs = Cs(DYNAMIC_NAME).apply {
        setAnimated(true)
        callback = this@Graph
    }

    private fun setDynamicsDiagrams() {
        for (currency in currencies) {
            dynamicCs.putRate(currency.key, currency.value.second)
        }
        invalidate()
    }

    /**2-я*/
    private val dateInterval = DateInterval()
    /**3-я*/
    private val staticCs = Cs(STATIC_NAME).apply {
        setAnimated(false)
        callback = this@Graph
    }

    private val currencies = mutableMapOf<Cs.Colors, Pair<MutableList<LocalDate>, MutableList<Float>>>()

    private val paint = Paint()

    /**Colors*/
    private val backgroundColor = resources.getColor(android.R.color.background_light)
    private val shadowColor = resources.getColor(R.color.colorShadow)
    private val dateColor = resources.getColor(R.color.colorPrimaryText)
    private val colorAxisText = resources.getColor(R.color.colorPrimary)
    private val colorAxisLine =
        Color.argb(30, Color.red(colorAxisText), Color.green(colorAxisText), Color.blue(colorAxisText))

    /**Dimensions*/
    private val smallPadding = MetricsConverter.dpToPx(4f, context)
    private val padding = MetricsConverter.dpToPx(8f, context)
    private val largePadding = MetricsConverter.dpToPx(16f, context)

    private val smallSizeText = MetricsConverter.spToPx(10f, context)
    private val sizeText = MetricsConverter.spToPx(14f, context)
    private val axisStrokeWidth = MetricsConverter.dpToPx(1f, context)

    private var dashInterval =
        floatArrayOf(MetricsConverter.dpToPx(15f, context), MetricsConverter.dpToPx(15f, context))
    private var dynamicStrokeWidth = MetricsConverter.dpToPx(1.5f, context)
    private var staticStrokeWidth = MetricsConverter.dpToPx(1f, context)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        selectDateData.setSize(0f, width.toFloat(), 0f, height * 0.1f)
        dateInterval.setSize(0f, width.toFloat(), height * 0.7f, height * 0.8f)
        dynamicCs.setSize(0f, width.toFloat(), height * 0.1f, height * 0.7f)
        dynamicCs.setExtremes(50f, 0f, 50f, 0f)
        staticCs.setSize(padding, width - padding, height * 0.8f, height * 0.95f)
    }

    /**Draw*/
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val start = System.currentTimeMillis()
        canvas?.let {
            selectDateData.drawData(it)

            dateInterval.drawDates(it)

            dynamicCs.drawBackground(it)
            dynamicCs.drawY1(it)
            dynamicCs.drawDiagrams(it)

            staticCs.drawBackground(it)
            staticCs.drawDiagrams(it)
            isWait = false
        }

        Log.i(TAG, "onDraw Time = ${System.currentTimeMillis() - start}")
    }

    /**Rectangle*/
    private fun Rectangle.drawBackground(canvas: Canvas) {
        paint.setBackground()
        canvas.drawRect(getRectF(), paint)
    }

    private fun DateInterval.drawDates(canvas: Canvas) {
        drawBackground(canvas)
        paint.setDateText()
        val dateTo = getDateTo()
        val textWidth = paint.measureText(dateTo)
        val bottomPadding = bottom() - (height() - paint.getTextHeight()) / 2
        canvas.drawText(getDateFrom(), left() + padding, bottomPadding, paint)
        canvas.drawText(dateTo, right() - padding - textWidth, bottomPadding, paint)
    }

    private fun DateRange.drawData(canvas: Canvas) {
        drawBackground(canvas)
        paint.setDateText()
        val firstCurrency = getFirstCurrency()
        val secondCurrency = getSecondCurrency()
        val p = paint.measureText(firstCurrency) + padding
        val bottomPadding = bottom() - (height() - paint.getTextHeight()) / 2
        canvas.drawText(getDate(), padding, bottomPadding, paint)
        canvas.drawText(secondCurrency, right() - p, bottomPadding, paint)
        canvas.drawText(firstCurrency, right() - p * 2, bottomPadding, paint)
    }
/*
    private fun Cs.drawY(canvas: Canvas) {
        var y = getFirstMarkerY()
        while (y < maxY()) {
            val pointY = coordY(y)
            val text = AppNumFormatter.AXIS.get()?.format(y) ?: ""
            paint.setAxisText()
            canvas.drawText(text, left() + smallPadding, pointY, paint)
            val textWidth = paint.measureText(text)
            paint.setAxisLine()
            canvas.drawLine(textWidth + padding, pointY, right() - smallPadding, pointY, paint)
            y += intervalY()
        }
    }*/

    private fun Cs.drawY1(canvas: Canvas) {
        val s = getTagsOnTheScale()
        s.forEach {
            val text = AppNumFormatter.AXIS.get()?.format(it.first) ?: ""
            paint.setAxisText()
            canvas.drawText(text, left() + smallPadding, it.second, paint)
            val textWidth = paint.measureText(text)
            paint.setAxisLine()
            canvas.drawLine(textWidth + largePadding, it.second, right() - smallPadding, it.second, paint)
        }
    }

    private fun Cs.drawDiagrams(canvas: Canvas) {
        if (name == DYNAMIC_NAME) {
            paint.setDynamic()
            createDiagrams()
        } else {
            paint.setStatic()
        }
        for (rate in getRates()) {
            paint.color = resources.getColor(rate.key.color)
            canvas.drawPath(rate.value.second, paint)
        }
    }

    /***/
    fun setCurrency(data: GraphElement, color: Cs.Colors) {
        currencies[color] = Pair(data.dates, data.rates)
        staticCs.putRate(color, data.rates)
    }

    fun delCurrency(color: Cs.Colors) {
        currencies.remove(color)
        staticCs.delRate(color)
        dynamicCs.delRate(color)
    }

    /**Utils
     * Paint*/
    private fun Paint.setBackground() {
        reset()
        style = Paint.Style.FILL
        color = backgroundColor
    }

    private fun Paint.setShadowBackground(radius: Float) {
        reset()
        style = Paint.Style.FILL
        color = backgroundColor
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            setShadowLayer(radius, 0f, 0f, shadowColor)
        }
    }

    private fun Paint.setDateText() {
        reset()
        style = Paint.Style.FILL
        color = dateColor
        textSize = sizeText
    }

    private fun Paint.setAxisText() {
        reset()
        style = Paint.Style.FILL
        color = colorAxisText
        strokeWidth = axisStrokeWidth
        textSize = smallSizeText
    }

    private fun Paint.setAxisLine() {
        reset()
        style = Paint.Style.STROKE
        strokeWidth = axisStrokeWidth
        color = colorAxisLine
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            pathEffect = DashPathEffect(dashInterval, 0f)
        }
    }

    private fun Paint.getTextHeight(): Float = fontMetrics.descent - fontMetrics.ascent

    private fun Paint.setDynamic() {
        reset()
        strokeWidth = dynamicStrokeWidth
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(dynamicStrokeWidth * 3)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            setShadowLayer(dynamicStrokeWidth * 5, dynamicStrokeWidth * 4f, dynamicStrokeWidth * 4f, shadowColor)
        }
    }

    private fun Paint.setStatic() {
        reset()
        strokeWidth = staticStrokeWidth
        style = Paint.Style.STROKE
    }

    /**GraphElement*/
    fun GraphElement.set(element: GraphElement) {
        id = element.id
        code = element.code
        rates = element.rates
        dates = element.dates
    }
}