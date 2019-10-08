package ru.tatarchuk.rest.request

import org.threeten.bp.LocalDate
import ru.tatarchuk.util.AppDateFormatter

class DynamicRequest(private val id: String, private val first: LocalDate, private val last: LocalDate) : Request() {

    private val idKey = "VAL_NM_RQ"
    private val firstKey = "date_req1"
    private val lastKey = "date_req2"

    override fun onMapCreate(map: MutableMap<String, String>) {
        map.apply {
            map[firstKey] = first.format(AppDateFormatter.requestDateFormat)
            map[lastKey] = last.format(AppDateFormatter.requestDateFormat)
            map[idKey] = id
        }
    }
}