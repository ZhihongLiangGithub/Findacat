package edu.gwu.zhihongliang.findacat.api

import android.util.Log
import edu.gwu.zhihongliang.findacat.Const
import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.model.schema.PetfinderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

class Petfinder(private val onCompleteListener: OnCompleteListener) {

    private val TAG = "Petfinder"

    interface apiEndpointInterface {
        @GET("pet.find")
        fun getPetFind(@QueryMap params: Map<String, String>): Call<PetfinderResponse>
    }

    interface OnCompleteListener {
        fun petfinderSuccess(petfinderResponse: PetfinderResponse)
        fun petfinderFail()
    }

    fun getPetFindDataByZip(zip: String) {
        val apiEndPoint = RetrofitManager.getPetfinderInstance().create(apiEndpointInterface::class.java)
        val params = hashMapOf(
                "key" to Const.PETFINDER_API_KEY,
                "format" to Const.PETFINDER_RESPONSE_FORMAT,
                "animal" to "cat",
                "location" to zip
        )
        apiEndPoint.getPetFind(params).enqueue(object : Callback<PetfinderResponse> {
            override fun onFailure(call: Call<PetfinderResponse>?, t: Throwable?) {
                Log.e(TAG, "Petfinder Api failure!", t)
                onCompleteListener.petfinderFail()
            }

            override fun onResponse(call: Call<PetfinderResponse>?, response: Response<PetfinderResponse>?) {
                val petfinderResponse = response?.body()
                        ?: return onCompleteListener.petfinderFail()
                onCompleteListener.petfinderSuccess(petfinderResponse)
            }
        })
    }
}