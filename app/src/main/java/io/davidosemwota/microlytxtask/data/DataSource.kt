/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Looper
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
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
import io.davidosemwota.microlytxtask.util.extentions.logd

@SuppressLint("MissingPermission")
object DataSource {

    fun initialise(
        telephonyManager: TelephonyManager,
        viewModel: HomeViewModel,
        context: Context
    ) {
        val mobileCountryCode = telephonyManager.networkCountryIso
        val mobileNetworkCode = telephonyManager.networkOperator
        val networkOperator = telephonyManager.simOperatorName

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        requestLocation(fusedLocationClient, viewModel)
        addNetworkAndCountryCode(mobileCountryCode, mobileNetworkCode, viewModel)
        addNetworkDetails(telephonyManager, context, viewModel)
        addNetWorkOperator(networkOperator, viewModel)
        addDeviceDetails(viewModel)
        addLocation(viewModel, context, fusedLocationClient)
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

        task.addOnSuccessListener {
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

    private fun addNetworkDetails(
        telephonyManager: TelephonyManager,
        context: Context,
        viewModel: HomeViewModel
    ) {
        val cellLocation = telephonyManager.allCellInfo

        viewModel.addPhoneDetail(
            PhoneDetail("Cell Connection Status", "Not Connected")
        )

        for (info in cellLocation) {
            when (info) {
                is CellInfoGsm -> {

                    if (info.isRegistered) {
                        viewModel.addPhoneDetail(
                            PhoneDetail(context.getString(R.string.mobile_network_tech), "GSM")
                        )
                        viewModel.addPhoneDetail(
                            PhoneDetail(
                                "Signal Strength",
                                "${info.cellSignalStrength.dbm} dBm"
                            )
                        )

                        viewModel.addPhoneDetail(
                            PhoneDetail("Cell Connection Status", "Connected",),
                            true
                        )
                    }
                }

                is CellInfoWcdma -> {
                    logd("HomeFragment", "3G matched")
                    if (info.isRegistered) {
                        viewModel.addPhoneDetail(
                            PhoneDetail(context.getString(R.string.mobile_network_tech), "3G")
                        )
                        val lac = info.cellIdentity.lac
                        val cellIdentity = info.cellIdentity.cid

                        viewModel.addPhoneDetail(
                            PhoneDetail("Local Area Code (LAC", lac.toString())
                        )

                        viewModel.addPhoneDetail(
                            PhoneDetail("Cell Identity (CID)", cellIdentity.toString())
                        )

                        viewModel.addPhoneDetail(
                            PhoneDetail(
                                "Signal Strength",
                                "${info.cellSignalStrength.dbm} dBm"
                            )
                        )

                        viewModel.addPhoneDetail(
                            PhoneDetail("Cell Connection Status", "Connected"),
                            true
                        )
                    }
                }

                is CellInfoLte -> {

                    if (info.isRegistered) {
                        viewModel.addPhoneDetail(
                            PhoneDetail(context.getString(R.string.mobile_network_tech), "LTE")
                        )
                        val cellIdentity = info.cellIdentity.ci
                        val tac = info.cellIdentity.tac

                        viewModel.addPhoneDetail(
                            PhoneDetail("Local Area Code (LAC)", tac.toString())
                        )
                        viewModel.addPhoneDetail(
                            PhoneDetail("Cell ID (CI)", cellIdentity.toString())
                        )

                        viewModel.addPhoneDetail(
                            PhoneDetail(
                                "Signal Strength",
                                "${info.cellSignalStrength.dbm} dBm"
                            )
                        )

                        viewModel.addPhoneDetail(
                            PhoneDetail("Cell Connection Status", "Connected"),
                            true
                        )
                    }
                }

                else -> {

                    viewModel.addPhoneDetail(
                        PhoneDetail("Cell Connection Status", "Not Connected"),
                        true
                    )
                }
            }
        }
    }
}
