package edu.gwu.zhihongliang.findacat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.RuntimeExecutionException
import edu.gwu.zhihongliang.findacat.Const
import edu.gwu.zhihongliang.findacat.PersistenceManager
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.api.PetfinderApiEndpoint
import edu.gwu.zhihongliang.findacat.model.CatInfo
import edu.gwu.zhihongliang.findacat.model.schema.PetfinderResponse
import edu.gwu.zhihongliang.findacat.ui.activity.MainActivity
import edu.gwu.zhihongliang.findacat.ui.activity.PetDetailActivity
import edu.gwu.zhihongliang.findacat.ui.adapter.CatInfoItemAdapter
import edu.gwu.zhihongliang.findacat.util.AddressUtil
import kotlinx.android.synthetic.main.fragment_find.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FindFragment : Fragment(), CatInfoItemAdapter.OnItemClickListener {

    private val TAG = "FindFragment"
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient
    private lateinit var catInfoList: MutableList<CatInfo>
    private lateinit var persistenceManager: PersistenceManager

    companion object {
        @JvmStatic
        fun newInstance() = FindFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlaceDetectionClient = Places.getPlaceDetectionClient(activity)
        persistenceManager = PersistenceManager(activity)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //turn on progress bar
        progressBar.visibility = View.VISIBLE
        //update cats
        if (arguments != null) {
            var zip = arguments.getString(MainActivity.KEY_SEARCH_ZIP)
            if (zip != null) {
                loadPetFindDataByZip(zip)
            }
        } else {
            updateCatsByCurrentLocation()
        }
        //set up swipe refresh for RecyclerView
        swipRefresh.setOnRefreshListener {
            updateCatsByCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateCatsByCurrentLocation() {
        try {
            mPlaceDetectionClient.getCurrentPlace(null).addOnCompleteListener {
                val result = it.result
                if (result != null) {
                    //get current address
                    val address = result[0].place.address.toString()
                    Log.i(TAG, "Address from Google API: $address")
                    var zip = AddressUtil.extractZipCodeFromAddress(address)
                    when (zip.isEmpty()) {
                        true -> {
                            Log.e(TAG, "unable to extract zip, use last available zip")
                            zip = persistenceManager.getZip()
                        }
                        false -> persistenceManager.saveZip(zip)
                    }
                    Log.i(TAG, "zip: $zip")
                    loadPetFindDataByZip(zip)
                    result.release()
                } else {
                    //get last zip
                    val zip = persistenceManager.getZip()
                    loadPetFindDataByZip(zip)
                }
            }
        } catch (e: RuntimeExecutionException) {
            Log.e(TAG, "Google Api failure", e)

        }
    }


    private fun loadPetFindDataByZip(zip: String) {
        val params = hashMapOf(
                "key" to Const.PETFINDER_API_KEY,
                "format" to Const.PETFINDER_RESPONSE_FORMAT,
                "animal" to "cat",
                "location" to zip
        )
        PetfinderApiEndpoint.apiEndPoint.getPetFind(params).enqueue(object : Callback<PetfinderResponse> {
            override fun onFailure(call: Call<PetfinderResponse>?, t: Throwable?) {
                Log.e(TAG, "Petfinder Api failure!", t)
                //TODO handle this
                //turn off progress bar
                if (progressBar != null) progressBar.visibility = View.INVISIBLE
                //turn off refreshing circle
                if (swipRefresh != null) swipRefresh.isRefreshing = false
            }

            override fun onResponse(call: Call<PetfinderResponse>, response: Response<PetfinderResponse>) {
                val body = response.body()
                if (body != null) {
                    catInfoList = body.petfinder.pets.pet.map { CatInfo.adaptedFrom(it) } as MutableList
                    //set adapter for RecyclerView
                    if (!catInfoList.isEmpty()) {
                        if (catInfo_rv != null) {
                            catInfo_rv.adapter = CatInfoItemAdapter(catInfoList, activity, this@FindFragment)
                        }
                    }
                    //turn off progress bar
                    if (progressBar != null) progressBar.visibility = View.INVISIBLE
                    //turn off refreshing circle
                    if (swipRefresh != null) swipRefresh.isRefreshing = false
                } else {
                    Log.e(TAG, "Petfinder Api failure!")
                    //TODO handle this
                    //turn off progress bar
                    if (progressBar != null) progressBar.visibility = View.INVISIBLE
                    //turn off refreshing circle
                    if (swipRefresh != null) swipRefresh.isRefreshing = false
                }
            }
        })
    }

    override fun onItemClick(catInfo: CatInfo, itemView: View) {
        val intent = PetDetailActivity.newIntent(activity, catInfo)
        startActivity(intent)
    }


}
