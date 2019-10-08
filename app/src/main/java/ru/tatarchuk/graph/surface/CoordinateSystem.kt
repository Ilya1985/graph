package ru.tatarchuk.graph.surface

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CoordinateSystem(private val colorPrimary: Int, private val colorSecondary: Int, private val txtSize: Float) {

    companion object {
        private val TAG = "<>${CoordinateSystem::class.java.simpleName}"
    }

    private val axisX = Axis()
    private val axisY = Axis()

    private val paint = Paint()

    // Для отображения пунктира у значений по оси Y
    private val path = Path()

    private val dayFormat = DecimalFormat("00")
    private val rateFormat = DecimalFormat("0.0", DecimalFormatSymbols(Locale.US))

    fun draw(canvas: Canvas) {

        /** Находит ближайшее кратное getXStep(шагу) Это нухно для того, чтобы пунктирные линии на графике
         * для значенией оси Y риовалисть только для значений кратных шагу*/
        /*   var Abscissa = axisX.getMin + (getXStep() - (axisX.getMin % getXStep()))

           /** Далее наносятся значения на ось Х с частотой, которую возвращает getXStep*/
           while (Abscissa <= axisX.getMax) {
               val pointX = coordX(Abscissa)
               canvas.drawLine(pointX, axisY.size, pointX, axisY.size - 5f, paint)
               canvas.drawText(dayFormat.format(Abscissa), pointX - 18, axisY.size - 7f, paint)
               Abscissa += getXStep()
           }*/

        /**По оси Y все аналогично, за исключение нанесения пунктира для значений*/
        var y = axisY.min + (getYStep() - (axisY.min % getYStep()))

        while (y <= axisY.max) {
            val pointY = coordY(y)//axisY.getCoord(Ordinate)// axisY.size - (Ordinate - axisY.getMin) * axisY.scale()
            setPaintDefault() // Возпращает настройки по умолчанию
            canvas.drawText(rateFormat.format(y), 7f, pointY, paint)
            setPaintDash() // Настройки Paint для рисования пунктира
            path.reset()
            path.moveTo(80f, pointY - 10)
            path.lineTo(axisX.size, pointY - 10)
            canvas.drawPath(path, paint)

            y += getYStep()
        }
    }

    val coordY: (Float) -> Float = { y -> axisY.size - (y - axisY.min) * axisY.scale }

    val coordX: (Float) -> Float = { x -> (x - axisX.min) * axisX.scale }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun setTextSize(textSize: Float) {
        paint.textSize = textSize
    }

    fun scaleX() = axisX.scale

    fun scaleY() = axisY.scale

    fun setMaxX(value: Float) {

        axisX.max = value
    }

    fun setMinX(value: Float) {
        axisX.min = value
    }

    fun setMaxY(value: Float) {
        axisY.max = value
    }

    fun setMinY(value: Float) {
        axisY.min = value
    }

    fun setHeight(height: Int) {
        axisY.size = height.toFloat()
    }

    fun setWidth(width: Int) {
        axisX.size = width.toFloat()
    }

    fun setDuration(duration: Long) {
        axisX.iDuration = duration
        axisY.iDuration = duration
    }

    fun setInterpolator(interpolator: TimeInterpolator) {
        axisX.iInterpolator = interpolator
        axisY.iInterpolator = interpolator
    }

    /**Определяет шаг, с которым будут отображаться значения на оси*/

    private fun getXStep(): Float {
        val dif = axisX.diff()
        return when {
            dif > 0 && dif <= 7 -> 1f
            dif > 7 && dif <= 31 -> 5f
            dif > 31 -> 10f
            else -> 0f
        }
    }

    private fun getYStep(): Float {
        val dif = axisY.diff()
        return when {
            dif > 0 && dif <= 1 -> 0.2f
            dif > 1 && dif <= 5 -> 0.5f
            dif > 5 && dif <= 10 -> 1f
            dif > 10 && dif <= 20 -> 2.5f
            dif > 20 && dif <= 50 -> 5f
            dif > 50 -> 20f
            else -> 0f
        }
    }

    /**Слить для рисования оси коордитат*/
    private fun setPaintDefault() {
        paint.apply {
            reset()
            style = Paint.Style.FILL
            strokeWidth = 5f
            color = colorPrimary
            textSize = txtSize
        }
    }

    /**Стиль для рисования пунктирных линий*/
    private fun setPaintDash() {
        paint.apply {
            textSize = txtSize
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = colorSecondary
            pathEffect = DashPathEffect(floatArrayOf(30f, 20f), 0f)
        }
    }
}