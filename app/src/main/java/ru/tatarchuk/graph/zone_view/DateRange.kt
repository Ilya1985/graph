package ru.tatarchuk.graph.zone_view

import org.threeten.bp.LocalDate
import ru.tatarchuk.util.AppDateFormatter
import ru.tatarchuk.util.AppNumFormatter

class DateRange : Rectangle() {

    var selectedDate: LocalDate? = null
    var firstCurrency = 0f
    var secondCurrency = 0f

    fun getDate() = AppDateFormatter.getViewDate(selectedDate)

    fun getFirstCurrency() = AppNumFormatter.RATE.get()?.format(firstCurrency) ?: ""

    fun getSecondCurrency() = AppNumFormatter.RATE.get()?.format(secondCurrency) ?: ""

}