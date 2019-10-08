package ru.tatarchuk.util

import java.text.DecimalFormat

object AppNumFormatter {

    var RATE: ThreadLocal<DecimalFormat> = object : ThreadLocal<DecimalFormat>() {
        override fun initialValue(): DecimalFormat {
            return DecimalFormat("0.0000")
        }
    }

    var AXIS: ThreadLocal<DecimalFormat> = object : ThreadLocal<DecimalFormat>(){
        override fun initialValue(): DecimalFormat? {
            return DecimalFormat("00.00")
        }
    }
}