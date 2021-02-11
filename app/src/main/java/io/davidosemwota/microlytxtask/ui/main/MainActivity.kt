/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.main

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.davidosemwota.microlytxtask.databinding.ActivityMainBinding
import io.davidosemwota.microlytxtask.util.extentions.logd

class MainActivity : AppCompatActivity() {

    private val locationManager: LocationManager by lazy {
        applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }
    private val isLocationEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestToEnableLocation()
    }

    override fun onRestart() {
        super.onRestart()

        logd("HomeFragme", "onRestart")
    }

    private fun requestToEnableLocation() {
        if (!isLocationEnabled) {
            val enableLocationDialog = AlertDialog.Builder(this)
                .setTitle("Location services not active")
                .setMessage("Please enable location services and GPS")
                .setPositiveButton("Ok") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }.create()
            enableLocationDialog.setCanceledOnTouchOutside(false)
            enableLocationDialog.show()
        }
    }
}
