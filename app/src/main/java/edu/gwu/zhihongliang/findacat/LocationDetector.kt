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
    private val expireDuration: Long = 10000
    private val expireHandler = Handler()
    private lateinit var expireRunnable: Runnable
    private var lastAddress: Address? = null

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 11
        const val REQUEST_CHECK_SETTINGS = 22
    }

    interface LocationUpdateResultHandler {
        fun locationUpdateSuccess(address: Address)
        fun locationUpdateFail()
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
        expireHandler.postDelayed(expireRunnable, expireDuration)
    }

    fun createLocationRequest(handler: LocationUpdateResultHandler) {
        expireRunnable = Runnable {
            Log.e(TAG, "location request time out!")
            fusedLocationClient.removeLocationUpdates(locationCallback)
            // fall back to last address if not null, otherwise fail
            lastAddress?.let {
                Log.e(TAG, "fall back to last address!")
                handler.locationUpdateSuccess(it)
            } ?: run {
                Log.e(TAG, "no last address available!")
                handler.locationUpdateFail()
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0?.lastLocation?.let {
                    val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    expireHandler.removeCallbacks(expireRunnable)
                    if (address != null && address.isNotEmpty()) {
                        Log.i(TAG, "receive new address!")
                        lastAddress = address[0]
                        handler.locationUpdateSuccess(address[0])
                    } else {
                        // address is null or empty
                        Log.e(TAG, "address is empty!")
                        handler.locationUpdateFail()
                    }
                }
            }
        }

        locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            setExpirationDuration(expireDuration)
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
}