package ru.tatarchuk.rest.response

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
data class DynamicValCurs(

    @field:Attribute(name = "ID")
    var id: String = "",

    @field:Attribute(name = "DateRange1")
    var dateFrom: String = "",

    @field:Attribute(name = "DateRange2")
    var dateTo: String = "",

    @field:Attribute(name = "name")
    var name: String? = null,

    @field:ElementList(name = "Record", inline = true)
    var records: List<Record> = mutableListOf()
)