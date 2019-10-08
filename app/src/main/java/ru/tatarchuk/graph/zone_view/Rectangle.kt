package ru.tatarchuk.graph.zone_view

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.contains

open class Rectangle {

    private var background = RectF(0f, 0f, 0f, 0f)

    fun left() = background.left

    fun right() = background.right

    fun top() = background.top

    fun bottom() = background.bottom

    fun width() = background.width()

    fun height() = background.height()


    open fun setSize(left: Float, right: Float, top: Float, bottom: Float) {
        background.left = left
        background.right = right
        background.top = top
        background.bottom = bottom
    }

    fun contains(p: PointF): Boolean = background.contains(p)

    fun contains(x: Float, y: Float): Boolean = background.contains(x, y)

    fun getRectF() = background

    fun getRect() = Rect(left().toInt(), top().toInt(), right().toInt(), bottom().toInt())
}