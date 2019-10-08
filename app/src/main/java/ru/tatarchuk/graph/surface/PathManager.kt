package ru.tatarchuk.graph.surface

import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import ru.tatarchuk.graph.Element

class PathManager(private val pathColor: Int, private val shadowColor: Int) {

    companion object {
        private val TAG = "<>${PathManager::class.java.simpleName}"
    }

    var elements = mutableListOf<Element>()

    private val path = Path()

    val paint = Paint(0).apply {
        color = pathColor
        //maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.SOLID)
        strokeWidth = 5f
        isDither = true
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(15f)
        setShadowLayer(10f, 7f, 7f, shadowColor)
    }

    fun getMin(): Float {
        //.i(TAG, "${elements.minBy { it.value }?.value}")
        return elements.minBy { it.value }?.value ?: Float.MAX_VALUE
    }

    fun getMax(): Float? = elements.maxBy { it.value }?.value

    fun getSize(): Float = elements.size.toFloat()

    fun drawPath(getX: (Float) -> Float, getY: (Float) -> Float, canvas: Canvas) {
        path.reset()
        for ((index, element) in elements.withIndex()) {
            val y = getY(element.value)
            val x = getX(index.toFloat() + 1)
            when (index) {
                0 -> path.moveTo(x, y)
                else -> path.lineTo(x, y)
            }
        }
        canvas.drawPath(path, paint)
    }
}