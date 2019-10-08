package ru.tatarchuk.graph.view.coordinates

import android.content.Context
import android.graphics.*
import android.util.Log
import ru.tatarchuk.R
import ru.tatarchuk.util.MetricsConverter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CoordSys(val name: String, context: Context) : Axiss.Callback {

    companion object {
        private val TAG = "<>${CoordSys::class.java.simpleName}"
    }

    /**4 зоны. Каждая на всю ширину view*/

    /**1-я данные на выбранную дату - 20dp, это 5% от высоты всей view*/

    private val abscissa = Axiss(Axiss.Type.Abscissa, this)
    private val ordinate = Axiss(Axiss.Type.Ordinate, this)

    var callback: CoordSysCallback? = null

    private val rateFormat = DecimalFormat("0.0", DecimalFormatSymbols(Locale.US))

    /**Цвет для отрисовки цифр на осях*/
    var colorAxes = context.resources.getColor(R.color.colorPrimary)
    /**Цвет пунктира */
    var colorDash = Color.argb(30, Color.red(colorAxes), Color.green(colorAxes), Color.blue(colorAxes))
    /**Размер цифр*/
    private var sizeText = MetricsConverter.spToPx(10f, context)
    /**Толцина линии цифр*/
    private var textStrokeWidth = MetricsConverter.dpToPx(1f, context)
    /**Толищина линий пунктира*/
    private var dashStrokeWidth = MetricsConverter.dpToPx(1f, context)
    /**Интервал пунктира*/
    private var dashInterval =
        floatArrayOf(MetricsConverter.dpToPx(15f, context), MetricsConverter.dpToPx(15f, context))

    private var padding = MetricsConverter.dpToPx(4f, context)

    private val paint = Paint()

    override fun onScaleChange() {
        callback?.redraw(name)
    }

    val getCoord: (Int, Float) -> Pair<Float, Float> =
        { x, y -> Pair(abscissa.getCoord(x.toFloat()), ordinate.getCoord(y)) }

    interface CoordSysCallback {
        fun redraw(name: String)
    }

    fun drawAxes(canvas: Canvas) {
        drawY(canvas)
        drawX(canvas)
        paint.pathEffect = null
        canvas.drawLine(abscissa.startPoint, ordinate.endPoint, abscissa.endPoint, ordinate.endPoint, paint)
    }

    private fun drawY(canvas: Canvas) {
        var y = ordinate.firstMarker()
        while (y < ordinate.getMax()) {
            val pointY = ordinate.getCoord(y)
            setPaintText()
            canvas.drawText(rateFormat.format(y), abscissa.startPoint, pointY - padding, paint)
            setPaintDash() // Настройки Paint для рисования пунктира
            canvas.drawLine(
                abscissa.startPoint,
                pointY,
                abscissa.endPoint,
                pointY,
                paint
            )
            y += ordinate.interval()
        }
    }

    private fun drawX(canvas: Canvas) {

    }

    private fun setPaintDash() {
        paint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeWidth = dashStrokeWidth
            color = colorDash
            pathEffect = DashPathEffect(dashInterval, 0f)
        }
    }

    private fun setPaintText() {
        paint.apply {
            reset()
            style = Paint.Style.FILL
            strokeWidth = textStrokeWidth
            color = colorAxes
            textSize = sizeText
        }
    }

    fun changeSize(startX: Float, endX: Float, startY: Float, endY: Float) {
        Log.i(TAG, "New sizes! X{$startX, $endX} Y{$startY, $endY}")
        abscissa.startPoint = startX
        abscissa.endPoint = endX
        ordinate.startPoint = startY
        ordinate.endPoint = endY
    }

    fun setExtremes(minX: Float, maxX: Float, minY: Float, maxY: Float) {
        Log.i(TAG, "New extremes! X{$minX, $maxX} Y{$minY, $maxY}")
        abscissa.setMin(minX)
        abscissa.setMax(maxX)
        ordinate.setMin(minY)
        ordinate.setMax(maxY)
    }

    fun setAnimated(isOn: Boolean) {
        abscissa.setAnimated(isOn)
        ordinate.setAnimated(isOn)
    }
}