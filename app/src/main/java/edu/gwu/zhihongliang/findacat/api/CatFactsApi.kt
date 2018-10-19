package edu.gwu.zhihongliang.findacat.api

import android.util.Log
import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

class CatFactsApi(private val onCompleteListener: OnCompleteListener) {

    private val TAG = "CatFacts"

    interface apiEndpointInterface {
        @GET("fact")
        fun getFact(): Call<CatFactResponse>
    }

    interface OnCompleteListener {
        fun catFactSuccess(fact: String)
        fun catFactFail()
    }

    fun getFactData() {
        val apiEndPoint = RetrofitManager.getCatFactsInstance()
                .create(apiEndpointInterface::class.java)
        apiEndPoint.getFact().enqueue(object : Callback<CatFactResponse> {
            override fun onFailure(call: Call<CatFactResponse>?, t: Throwable?) {
                Log.e(TAG, "Cat Fact Api failure!", t)
                onCompleteListener.catFactFail()
            }

            override fun onResponse(call: Call<CatFactResponse>?, response: Response<CatFactResponse>?) {
                response?.body()?.fact?.let {
                    onCompleteListener.catFactSuccess(it)
                } ?: onCompleteListener.catFactFail()
            }
        })
    }
}