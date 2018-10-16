package edu.gwu.zhihongliang.findacat.api

import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.model.schema.PetfinderResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

class PetfinderApiEndpoint {
    interface PetfinderApiEndpointInterface {
        @GET("pet.find")
        fun getPetFind(@QueryMap params: Map<String, String>): Call<PetfinderResponse>
    }

    companion object {
        val apiEndPoint = RetrofitManager.getPetfinderInstance().create(PetfinderApiEndpointInterface::class.java)
    }
}