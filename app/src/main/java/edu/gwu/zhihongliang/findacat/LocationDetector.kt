package edu.gwu.zhihongliang.findacat

import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Handler
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
    private val mInterval: Long = 20000
    private val mFastestInterval: Long = 10000
    private val mExpireDuration: Long = 10000
    private val expireHandler = Handler()
    private lateinit var expireRunnable: Runnable
    private var lastAddress: Address? = null

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 11
        const val REQUEST_CHECK_SETTINGS = 22
    }

    interface OnGetCurrentLocationCompleteListener {
        fun getCurrentLocationSuccess(address: Address)
        fun getCurrentLocationFail()
    }

    interface LocationUpdateResultHandler {
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
                    lastAddress = address[0]
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
        //expireHandler.postDelayed(expireRunnable, mExpireDuration)
    }

    fun createLocationRequest(handler: LocationUpdateResultHandler) {
        expireRunnable = Runnable {
            Log.e(TAG, "location request time out!")
            // fall back to last address if not null, otherwise fail
            lastAddress?.let { handler.locationUpdateSuccess(it) } ?: handler.locationUpdateFail()
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0?.lastLocation?.let {
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (address != null && address.isNotEmpty()) {
                        lastAddress = address[0]
                        //expireHandler.removeCallbacks(expireRunnable)
                        handler.locationUpdateSuccess(address[0])
                    } else handler.locationUpdateFail()
                }
            }
        }

        locationRequest = LocationRequest().apply {
            interval = 20000
            fastestInterval = 10000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            //setExpirationDuration(2000)
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

    fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}