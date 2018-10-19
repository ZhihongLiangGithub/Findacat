package edu.gwu.zhihongliang.findacat.ui.fragment

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.gwu.zhihongliang.findacat.LocationDetector
import edu.gwu.zhihongliang.findacat.R
import edu.gwu.zhihongliang.findacat.api.PetfinderApi
import edu.gwu.zhihongliang.findacat.model.CatInfo
import edu.gwu.zhihongliang.findacat.model.schema.Pets
import edu.gwu.zhihongliang.findacat.ui.activity.MainActivity
import edu.gwu.zhihongliang.findacat.ui.activity.PetDetailActivity
import edu.gwu.zhihongliang.findacat.ui.adapter.CatInfoItemAdapter
import edu.gwu.zhihongliang.findacat.util.ConnectivityUtil
import edu.gwu.zhihongliang.findacat.util.NotifyUtil
import kotlinx.android.synthetic.main.fragment_find.*


class FindFragment : Fragment(),
        CatInfoItemAdapter.OnItemClickListener,
        PetfinderApi.OnCompleteListener,
        LocationDetector.OnGetCurrentLocationCompleteListener,
        LocationDetector.locationUpdateResultHandler {

    private val TAG = "FindFragment"
    private lateinit var catInfoList: MutableList<CatInfo>
    private val petfinder = PetfinderApi(this)
    private lateinit var locationDetector: LocationDetector


    companion object {
        @JvmStatic
        fun newInstance() = FindFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationDetector = LocationDetector(activity)
        locationDetector.setLocationCallBackHandler(this)
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
        //update cats by search zip or by current location
        arguments?.getString(MainActivity.KEY_SEARCH_ZIP)?.let { petfinder.getPetFindDataByZip(it) }
                ?: locationDetector.createLocationRequest()
        //set up swipe refresh for RecyclerView
        swipRefresh.setOnRefreshListener {
            locationDetector.getCurrentLocation(this)
        }
    }

    override fun getCurrentLocationSuccess(address: Address) {
        address.postalCode?.let {
            Log.i(TAG, "current zip: $it")
            petfinder.getPetFindDataByZip(it)
        } ?: run {
            // postal code can be null
            NotifyUtil.showToast(context, getString(R.string.zip_unknown))
            getCurrentLocationFail()
        }
    }

    override fun getCurrentLocationFail() {
        Log.e(TAG, "get current location fail!")
        //TODO handle this
        turnOffProgressWidget()
    }

    override fun petfinderSuccess(pets: Pets) {
        catInfoList = mutableListOf()
        pets.pet.forEach {
            CatInfo.adaptedFrom(it)?.let { catInfo -> catInfoList.add(catInfo) }
        }
        //set adapter for RecyclerView
        if (catInfoList.isNotEmpty()) {
            catInfo_rv?.apply { adapter = CatInfoItemAdapter(catInfoList, activity, this@FindFragment) }
        } else {
            NotifyUtil.showToast(context, getString(R.string.no_cat_data_found))
        }
        turnOffProgressWidget()
    }

    override fun petfinderFail(message: String?) {
        Log.e(TAG, "Petfinder Api failure!")
        if (message != null) {
            NotifyUtil.showToast(context, message)
        } else if (!ConnectivityUtil.isConnected(context)) {
            NotifyUtil.internetNotConnected(context)
        } else {
            NotifyUtil.showToast(context, getString(R.string.petfinder_fail))
        }
        turnOffProgressWidget()
    }

    override fun locationUpdateSuccess(address: Address) {
        address.postalCode?.let {
            Log.i(TAG, "location update current zip: $it")
            petfinder.getPetFindDataByZip(it)
        } ?: run {
            // postal code can be null
            NotifyUtil.showToast(context, getString(R.string.zip_unknown))
            locationUpdateFail()
        }
    }

    override fun locationUpdateFail() {
        Log.e(TAG, "location update fail!")
        //TODO handle this
        turnOffProgressWidget()
    }

    override fun onItemClick(catInfo: CatInfo, itemView: View) {
        val intent = PetDetailActivity.newIntent(activity, catInfo)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LocationDetector.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationDetector.startLocationUpdates()
            }
        }
    }

    private fun turnOffProgressWidget() {
        //turn off progress bar
        progressBar?.apply { visibility = View.INVISIBLE }
        //turn off refreshing circle
        swipRefresh?.apply { isRefreshing = false }
    }

    override fun onDestroy() {
        locationDetector.removeLocationUpdates()
        super.onDestroy()
    }

}
