package ru.tatarchuk.rest.response

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Record", strict = false)
data class Record(

    @field:Attribute(name = "Date")
    var date: String = "",

    @field:Element(name = "Nominal")
    var nominal: Int = 1,

    @field:Element(name = "Value")
    var value: String = "0"
)