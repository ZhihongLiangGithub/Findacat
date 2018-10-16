package edu.gwu.zhihongliang.findacat.api

import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import retrofit2.Call
import retrofit2.http.GET

class CatFactsApiEndpoint {
    interface CatFactsApiEndpointInterface {
        @GET("fact")
        fun getFact(): Call<CatFactResponse>
    }

    companion object {
        val apiEndPoint = RetrofitManager.getCatFactsInstance()
                .create(CatFactsApiEndpointInterface::class.java)
    }
}