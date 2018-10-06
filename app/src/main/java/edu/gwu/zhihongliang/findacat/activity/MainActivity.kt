package edu.gwu.zhihongliang.findacat.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.api.CatFactsApiEndpointInterface
import edu.gwu.zhihongliang.findacat.model.schema.CatFactResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    companion object {
        const val FIND = "find"
        const val FAVOURITE = "favourite"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //load data from cat facts api
        loadCatFactData()
    }

    private fun loadCatFactData() {
        val retrofit = RetrofitManager.getCatFactsInstance()
        val apiEndPoint = retrofit.create(CatFactsApiEndpointInterface::class.java)
        apiEndPoint.getFact()
                .enqueue(object : Callback<CatFactResponse> {
                    override fun onFailure(call: Call<CatFactResponse>?, t: Throwable?) {
                        Log.e(TAG, "Cat Fact Api failure!", t)
                        // TODO handle this
                    }

                    override fun onResponse(call: Call<CatFactResponse>, response: Response<CatFactResponse>) {
                        val result = response.body()
                        if (result != null) {
                            cat_fact_tv.text = result.fact
                        } else {
                            Log.e(TAG, "Cat Fact Api failure!")
                            // TODO handle this
                        }
                    }
                })
    }

    fun findCatBtnClicked(view: View) {
        val intent = PetsActivity.newIntent(this, FIND)
        startActivity(intent)
    }

    fun favouriteCatsBtnClicked(view: View) {
        val intent = PetsActivity.newIntent(this, FAVOURITE)
        startActivity(intent)
    }


}
