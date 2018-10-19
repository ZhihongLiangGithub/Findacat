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
import edu.gwu.zhihongliang.findacat.api.Petfinder
import edu.gwu.zhihongliang.findacat.model.CatInfo
import edu.gwu.zhihongliang.findacat.model.schema.PetfinderResponse
import edu.gwu.zhihongliang.findacat.ui.activity.MainActivity
import edu.gwu.zhihongliang.findacat.ui.activity.PetDetailActivity
import edu.gwu.zhihongliang.findacat.ui.adapter.CatInfoItemAdapter
import edu.gwu.zhihongliang.findacat.util.NotifyUtil
import kotlinx.android.synthetic.main.fragment_find.*


class FindFragment : Fragment(),
        CatInfoItemAdapter.OnItemClickListener,
        Petfinder.OnCompleteListener,
        LocationDetector.OnGetCurrentLocationCompleteListener,
        LocationDetector.locationUpdateResultHandler {

    private val TAG = "FindFragment"
    private lateinit var catInfoList: MutableList<CatInfo>
    private lateinit var petfinder: Petfinder
    private lateinit var locationDetector: LocationDetector

    companion object {
        @JvmStatic
        fun newInstance() = FindFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petfinder = Petfinder(this)
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
        val zip = address.postalCode
                ?: return NotifyUtil.showToast(activity, getString(R.string.zip_unknown))
        Log.i(TAG, "current zip: $zip")
        petfinder.getPetFindDataByZip(zip)
    }

    override fun getCurrentLocationFail() {
        Log.e(TAG, "get current location fail!")
    }

    override fun petfinderSuccess(petfinderResponse: PetfinderResponse) {
        catInfoList = petfinderResponse.petfinder.pets.pet.map { CatInfo.adaptedFrom(it) } as MutableList
        //set adapter for RecyclerView
        if (!catInfoList.isEmpty()) {
            if (catInfo_rv != null) {
                catInfo_rv.adapter = CatInfoItemAdapter(catInfoList, activity, this)
            }
        }
        //turn off progress bar
        if (progressBar != null) progressBar.visibility = View.INVISIBLE
        //turn off refreshing circle
        if (swipRefresh != null) swipRefresh.isRefreshing = false
    }

    override fun petfinderFail() {
        Log.e(TAG, "Petfinder Api failure!")
        //TODO handle this
        //turn off progress bar
        if (progressBar != null) progressBar.visibility = View.INVISIBLE
        //turn off refreshing circle
        if (swipRefresh != null) swipRefresh.isRefreshing = false
    }

    override fun locationUpdateSuccess(address: Address) {
        val zip = address.postalCode
                ?: return NotifyUtil.showToast(activity, getString(R.string.zip_unknown))
        Log.i(TAG, "location update current zip: $zip")
        petfinder.getPetFindDataByZip(zip)
    }

    override fun locationUpdateFail() {
        Log.e(TAG, "location update fail!")
    }

    override fun onItemClick(catInfo: CatInfo, itemView: View) {
        val intent = PetDetailActivity.newIntent(activity, catInfo)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationDetector.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationDetector.startLocationUpdates()
            }
        }
    }

    override fun onDestroy() {
        locationDetector.removeLocationUpdates()
        super.onDestroy()
    }


}
