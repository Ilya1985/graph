package ru.tatarchuk.graph.zone_view

import org.threeten.bp.LocalDate
import ru.tatarchuk.util.AppDateFormatter

class DateInterval : Rectangle() {

    private val dateFormat = AppDateFormatter.viewDateFormat

    var dateFrom: LocalDate? = null

    var dateTo: LocalDate? = null

    fun getDateTo() = AppDateFormatter.getViewDate(dateTo)

    fun getDateFrom() = AppDateFormatter.getViewDate(dateFrom)

}