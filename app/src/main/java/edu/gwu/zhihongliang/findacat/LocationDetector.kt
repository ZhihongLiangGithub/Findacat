package edu.gwu.zhihongliang.findacat

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationDetector(private val activity: Activity) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private val geocoder = Geocoder(activity)

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    interface OnGetCurrentLocationCompleteListener {
        fun getCurrentLocationSuccess(address: Address)
        fun getCurrentLocationFail()
    }

    fun getCurrentLocation(listener: OnGetCurrentLocationCompleteListener) {
        // request location permission
        if (ActivityCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    it?.let {
                        val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (address != null && address.isNotEmpty()) {
                            listener.getCurrentLocationSuccess(address[0])
                        } else listener.getCurrentLocationFail()
                    } ?: listener.getCurrentLocationFail()
                }.addOnFailureListener {
                    listener.getCurrentLocationFail()
                }
    }
}