package edu.gwu.zhihongliang.findacat.api

import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import retrofit2.Call
import retrofit2.http.GET

interface CatFactsApiEndpointInterface {
    @GET("fact")
    fun getFact(): Call<CatFactResponse>
}