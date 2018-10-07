package edu.gwu.zhihongliang.findacat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import edu.gwu.zhihongliang.findacat.Const
import edu.gwu.zhihongliang.findacat.PersistenceManager
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.RetrofitManager
import edu.gwu.zhihongliang.findacat.adapter.CatAdapter
import edu.gwu.zhihongliang.findacat.api.PetfinderApiEndpointInterface
import edu.gwu.zhihongliang.findacat.model.CatInfo
import edu.gwu.zhihongliang.findacat.model.schema.PetfinderResponse
import kotlinx.android.synthetic.main.activity_pets.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PetsActivity : AppCompatActivity(), CatAdapter.OnItemClickListener {

    private val TAG = "PetsActivity"
    private val FAVOURITE_DETAIL_REQUEST = 1
    private val persistenceManager: PersistenceManager by lazy { PersistenceManager(this) }
    private lateinit var catInfoList: MutableList<CatInfo>
    private lateinit var type: String

    companion object {

        const val TYPE = "type"

        fun newIntent(context: Context, type: String): Intent {
            val intent = Intent(context, PetsActivity::class.java)
            intent.putExtra(TYPE, type)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)
        type = intent.getStringExtra(TYPE)
        when (type) {
            MainActivity.FIND -> displayFindCat()
            MainActivity.FAVOURITE -> displayFavouriteCat()
        }
    }

    private fun displayFindCat() {
        //TODO get current zip
        val zip = "22202"
        Log.i(TAG, "get current zip: $zip")
        // load cat data from petfinder api
        loadPetFindDataByZip(zip)
    }


    fun loadPetFindDataByZip(zip: String) {
        val retrofit = RetrofitManager.getPetfinderInstance()
        val apiEndPoint = retrofit.create(PetfinderApiEndpointInterface::class.java)
        val params = hashMapOf(
                "key" to Const.PETFINDER_API_KEY,
                "format" to Const.PETFINDER_RESPONSE_FORMAT,
                "animal" to "cat",
                "location" to zip
        )
        apiEndPoint.getPetFind(params).enqueue(object : Callback<PetfinderResponse> {
            override fun onFailure(call: Call<PetfinderResponse>?, t: Throwable?) {
                Log.e(TAG, "Petfinder Api failure!", t)
                //TODO handle this
            }

            override fun onResponse(call: Call<PetfinderResponse>, response: Response<PetfinderResponse>) {
                val body = response.body()
                if (body != null) {
                    catInfoList = body.petfinder.pets.pet.map { CatInfo.adaptedFrom(it) } as MutableList
                    //set adapter for RecyclerView
                    if (!catInfoList.isEmpty()) {
                        catInfo_rv.adapter = CatAdapter(catInfoList, this@PetsActivity)
                    }
                } else {
                    Log.e(TAG, "Petfinder Api failure!")
                    //TODO handle this
                }
            }
        })
    }


    private fun displayFavouriteCat() {
        catInfoList = persistenceManager.findAllFavouriteCats()
        if (!catInfoList.isEmpty()) {
            catInfo_rv.adapter = CatAdapter(catInfoList, this@PetsActivity)
        }
    }

    override fun onItemClick(catInfo: CatInfo, itemView: View) {
        val intent = PetDetailActivity.newIntent(this@PetsActivity, catInfo)
        when (type) {
            MainActivity.FIND -> startActivity(intent)
            MainActivity.FAVOURITE -> startActivityForResult(intent, FAVOURITE_DETAIL_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FAVOURITE_DETAIL_REQUEST) {
            if (resultCode == PetDetailActivity.RESULT_NOT_FAVOURITE) {
                val id = data?.getStringExtra("id")
                catInfoList.removeIf { it.id == id }
                catInfo_rv.adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        //save sharePreference
        if (type == MainActivity.FAVOURITE) {
            persistenceManager.saveFavouriteCats(catInfoList)
        }
        super.onBackPressed()
    }


}
