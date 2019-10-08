package ru.tatarchuk.rest.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.QueryMap
import ru.tatarchuk.rest.response.DynamicValCurs

interface DynamicApi {

    @GET("XML_dynamic.asp")
    fun getDynamic(@QueryMap map: Map<String, String>): Observable<DynamicValCurs>
}