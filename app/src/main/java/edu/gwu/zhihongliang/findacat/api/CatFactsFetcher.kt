package edu.gwu.zhihongliang.findacat.api

import android.util.Log
import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET

object CatFactsFetcher {

    private val TAG = "CatFactsFetcher"

    interface apiEndpointInterface {
        @GET("fact")
        fun getFact(): Call<CatFactResponse>
    }

    interface onCompleteListener {
        fun catFactSuccess(fact: String)
        fun catFactFail()
    }

    fun fetchData(onCompleteListener: onCompleteListener) {
        val apiEndPoint = RetrofitManager.getCatFactsInstance()
                .create(apiEndpointInterface::class.java)
        apiEndPoint.getFact()
                .enqueue(object : Callback<CatFactResponse> {
                    override fun onFailure(call: Call<CatFactResponse>?, t: Throwable?) {
                        Log.e(TAG, t?.message, t)
                        onCompleteListener.catFactFail()
                    }

                    override fun onResponse(call: Call<CatFactResponse>?, response: Response<CatFactResponse>?) {
                        val fact = response?.body()?.fact ?: return onCompleteListener.catFactFail()
                        onCompleteListener.catFactSuccess(fact)

                    }
                })
    }
}