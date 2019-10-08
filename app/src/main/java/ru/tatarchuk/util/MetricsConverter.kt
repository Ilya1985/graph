package ru.tatarchuk.util

import android.content.Context
import android.util.TypedValue

object MetricsConverter {

    fun dpToPx(dp: Float, context: Context) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    fun spToPx(sp: Float, context: Context) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)

    fun dpToSp(dp: Float, context: Context) =
        (dpToPx(dp, context) / context.resources.displayMetrics.scaledDensity).toInt()
}