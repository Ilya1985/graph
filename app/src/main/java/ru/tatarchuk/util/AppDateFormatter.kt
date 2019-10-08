package ru.tatarchuk.util

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import ru.tatarchuk.graph.zone_view.DateInterval

object AppDateFormatter {

    private const val EMPTY = "--/--"

    val requestDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val requestFormat: (LocalDate) -> String = {date -> date.format(requestDateFormat)}

    val responseDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val viewDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM")

    fun getViewDate(date: LocalDate?) = date?.format(viewDateFormat) ?: EMPTY

}