package ru.tatarchuk.graph

import org.threeten.bp.LocalDate

class GraphElement(){

    var id: String = ""
    var code: String = ""
    var rates = mutableListOf<Float>()
    var dates = mutableListOf<LocalDate>()

    constructor(id: String, code: String, rates: MutableList<Float>, dates: MutableList<LocalDate>): this(){
        this.id = id
        this.code = code
        this.rates = rates
        this.dates = dates
    }

    fun clear(){
        id = ""
        code = ""
        rates.clear()
        dates.clear()
    }
}