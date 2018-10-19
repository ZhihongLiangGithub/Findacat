package edu.gwu.zhihongliang.findacat

import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class LocationDetector(private val activity: Activity) {

    private val TAG = "LocationDetector"

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private val geocoder = Geocoder(activity)

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 11
        const val REQUEST_CHECK_SETTINGS = 22
    }

    interface OnGetCurrentLocationCompleteListener {
        fun getCurrentLocationSuccess(address: Address)
        fun getCurrentLocationFail()
    }

    interface locationUpdateResultHandler {
        fun locationUpdateSuccess(address: Address)
        fun locationUpdateFail()
    }

    fun getCurrentLocation(listener: OnGetCurrentLocationCompleteListener) {
        // request location permission
        if (ActivityCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener {
            it?.let {
                val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (address != null && address.isNotEmpty()) {
                    listener.getCurrentLocationSuccess(address[0])
                } else listener.getCurrentLocationFail()
            } ?: listener.getCurrentLocationFail()
        }
        task.addOnFailureListener {
            Log.e(TAG, it.message, it)
            listener.getCurrentLocationFail()
        }
    }

    fun startLocationUpdates() {
        locationUpdateState = true
        if (ActivityCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = 30000
            fastestInterval = 15000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun setLocationCallBackHandler(handler: locationUpdateResultHandler) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0?.lastLocation?.let {
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (address != null && address.isNotEmpty()) {
                        handler.locationUpdateSuccess(address[0])
                    } else handler.locationUpdateFail()
                }
            }
        }
    }

    fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}