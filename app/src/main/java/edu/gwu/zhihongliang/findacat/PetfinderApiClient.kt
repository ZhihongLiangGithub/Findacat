package edu.gwu.zhihongliang.findacat

import android.util.Log
import edu.gwu.zhihongliang.findacat.model.schema.PetfinderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap

class PetfinderApiClient {

    enum class Animal(val type: String) {
        CAT("cat")
    }

    companion object {
        val PETFINDER_API_KEY = "b93c0a99c51866c16cc8e333eaa14af0"
        val PETFINDER_URL = "https://api.petfinder.com"
    }

    private val TAG = "PetfinderApiClient"
    private val JSON = "json"

    interface ApiEndpointInterface {
        @GET("pet.find")
        fun getPetFind(@QueryMap params: Map<String, String>): Call<PetfinderResponse>
    }

    fun parsePetFindData(location: String) {
        val retrofit = Retrofit.Builder()
                .baseUrl(PETFINDER_URL)
                .addConverterFactory(MoshiConverterFactory.create(MoshiManager.getInstance()))
                .build()
        val apiEndPoint = retrofit.create(ApiEndpointInterface::class.java)
        val params = hashMapOf(
                "key" to PETFINDER_API_KEY,
                "format" to JSON,
                "animal" to Animal.CAT.type,
                "location" to location
        )
        val call = apiEndPoint.getPetFind(params)
        call.enqueue(object : Callback<PetfinderResponse> {
            override fun onFailure(call: Call<PetfinderResponse>?, t: Throwable?) {
                Log.e(TAG, "Petfinder Api failure!", t)
            }

            override fun onResponse(call: Call<PetfinderResponse>, response: Response<PetfinderResponse>) {
                response.body()?.let {

                }
            }
        })
    }

}