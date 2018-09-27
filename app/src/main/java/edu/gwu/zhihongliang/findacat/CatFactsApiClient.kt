package edu.gwu.zhihongliang.findacat

import android.content.Context
import android.util.Log
import android.widget.TextView
import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

class CatFactsApiClient(private val context: Context, private val textView: TextView) {

    companion object {
        val CATFACTS_URL = "https://catfact.ninja"
    }

    interface ApiEndpointInterface {
        @GET("fact")
        fun getFact(): Call<CatFactResponse>
    }

    private val TAG = "CatFactsApiClient"

    fun parseCatFactData() {
        val retrofit = Retrofit.Builder()
                .baseUrl(CATFACTS_URL)
                .addConverterFactory(MoshiConverterFactory.create(MoshiManager.getInstance()))
                .build()
        val apiEndPoint = retrofit.create(ApiEndpointInterface::class.java)
        apiEndPoint.getFact()
                .enqueue(object : Callback<CatFactResponse> {
                    override fun onFailure(call: Call<CatFactResponse>?, t: Throwable?) {
                        Log.e(TAG, "Cat Fact Api failure!", t)
                    }

                    override fun onResponse(call: Call<CatFactResponse>, response: Response<CatFactResponse>) {
                        response.body()?.let {
                            textView.text = it.fact
                        }
                    }
                })
    }
}