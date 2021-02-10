/*
 * MIT License
 *
 * Copyright (c) 2021   David Osemwota.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.davidosemwota.microlytxtask.data

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Looper
import android.telephony.CellInfoGsm
import android.telephony.TelephonyManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import io.davidosemwota.microlytxtask.R
import io.davidosemwota.microlytxtask.ui.home.HomeViewModel
import io.davidosemwota.microlytxtask.util.LATITUDE
import io.davidosemwota.microlytxtask.util.LONGITUDE

object DataSource {

    fun initialise(
        telephonyManager: TelephonyManager,
        viewModel: HomeViewModel,
        locationManager: LocationManager,
        context: Context
    ) {
        val mobileCountryCode = telephonyManager.networkCountryIso
        val mobileNetworkCode = telephonyManager.networkOperator
        val networkOperator = telephonyManager.simOperatorName

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        requestLocation(fusedLocationClient, viewModel)
        addNetworkAndCountryCode(mobileCountryCode, mobileNetworkCode, viewModel)
        addMobileTech(telephonyManager, viewModel, context)
        addSignalStrength(telephonyManager, context, viewModel)
        addNetWorkOperator(networkOperator, viewModel)
        addDeviceDetails(viewModel)
        addLocation(locationManager, viewModel, context, fusedLocationClient)
    }

    private fun requestLocation(
        fusedLocationClient: FusedLocationProviderClient,
        viewModel: HomeViewModel
    ) {

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // ...
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { bestLocation: Location? ->
                            viewModel.addPhoneDetail(
                                PhoneDetail(LONGITUDE, bestLocation?.longitude.toString()),
                                true
                            )
                            viewModel.addPhoneDetail(
                                PhoneDetail(LATITUDE, bestLocation?.latitude.toString()),
                                true
                            )
                        }
                }
            }
        }

        val locationRequest = LocationRequest.create()?.apply {
            interval = 500
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun addLocation(
        locationManager: LocationManager,
        viewModel: HomeViewModel,
        context: Context,
        fusedLocationClient: FusedLocationProviderClient
    ) {

        val locationRequest = LocationRequest.create()?.apply {
            interval = 500
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }
        builder?.setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener { _ ->
            // All location settings are satisfied. The client can initialize
            fusedLocationClient.lastLocation
                .addOnSuccessListener { bestLocation: Location? ->
                    viewModel.addPhoneDetail(
                        PhoneDetail(LONGITUDE, bestLocation?.longitude.toString())
                    )
                    viewModel.addPhoneDetail(
                        PhoneDetail(LATITUDE, bestLocation?.latitude.toString())
                    )
                }
        }
    }

    @Suppress("DEPRECATION")
    private fun addMobileTech(
        telephonyManager: TelephonyManager,
        viewModel: HomeViewModel,
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val network = telephonyManager.voiceNetworkType

            checkNetworkType(network, viewModel, context)
        } else {
            val network = telephonyManager.networkType
            checkNetworkType(network, viewModel, context)
        }
    }

    private fun addNetworkAndCountryCode(
        mobileCountryCode: String,
        mobileNetworkCode: String,
        viewModel: HomeViewModel
    ) {
        viewModel.addPhoneDetail(
            PhoneDetail("Mobile Country Code (MCC)", mobileCountryCode)
        )
        viewModel.addPhoneDetail(
            PhoneDetail("Mobile Network Code (MNC)", mobileNetworkCode)
        )
    }

    private fun addNetWorkOperator(networkOperator: String, viewModel: HomeViewModel) {
        viewModel.addPhoneDetail(
            PhoneDetail("Operator Name", networkOperator)
        )
    }

    private fun addDeviceDetails(viewModel: HomeViewModel) {
        viewModel.addPhoneDetail(
            PhoneDetail("Handset Make", Build.MANUFACTURER)
        )
        viewModel.addPhoneDetail(
            PhoneDetail("Item Model", Build.MODEL)
        )
    }

    private fun checkNetworkType(
        network: Int,
        viewModel: HomeViewModel,
        context: Context
    ) {
        val cm = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val allCellInfo = tm.allCellInfo

        when (network) {
            TelephonyManager.NETWORK_TYPE_LTE -> {
                viewModel.addPhoneDetail(
                    PhoneDetail(context.getString(R.string.mobile_network_tech), "LTE")
                )
            }

            TelephonyManager.NETWORK_TYPE_UMTS -> {
                viewModel.addPhoneDetail(
                    PhoneDetail(context.getString(R.string.mobile_network_tech), "3G")
                )

                for (info in allCellInfo) {
                    when (info) {
                        is CellInfoGsm -> {
                            if (info.isRegistered) {

                                Log.d("HomeFragment", "LAC is: ${info.cellIdentity.lac}")
                                Log.d("HomeFragment", "CID is: ${info.cellIdentity.cid}")
                            }
                        }
                    }
                }
            }

            TelephonyManager.NETWORK_TYPE_EDGE -> {
                viewModel.addPhoneDetail(
                    PhoneDetail(context.getString(R.string.mobile_network_tech), "EDGE")
                )
            }

            TelephonyManager.NETWORK_TYPE_GPRS -> {
                viewModel.addPhoneDetail(
                    PhoneDetail(context.getString(R.string.mobile_network_tech), "GPRS")
                )
            }

            TelephonyManager.NETWORK_TYPE_GSM -> {
                viewModel.addPhoneDetail(
                    PhoneDetail(context.getString(R.string.mobile_network_tech), "GSM")
                )

                for (info in allCellInfo) {
                    when (info) {
                        is CellInfoGsm -> {
                            if (info.isRegistered) {

                                Log.d("HomeFragment", "LAC is: ${info.cellIdentity.lac}")
                                Log.d("HomeFragment", "CID is: ${info.cellIdentity.cid}")
                            }
                        }
                    }
                }
            }

            TelephonyManager.NETWORK_TYPE_IWLAN -> {
            }
        }
    }

    private fun addSignalStrength(
        telephonyManager: TelephonyManager,
        context: Context,
        viewModel: HomeViewModel
    ) {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val currentNetwork = connectivityManager.activeNetwork
            val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
            val strength = caps?.signalStrength
            viewModel.addPhoneDetail(
                PhoneDetail("Signal Strength", strength.toString())
            )
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            val strength = telephonyManager.signalStrength
            viewModel.addPhoneDetail(
                PhoneDetail("Signal Strength 28", strength.toString())
            )
        }
    }
}
