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
package io.davidosemwota.microlytxtask

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.davidosemwota.microlytxtask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val locationManager: LocationManager by lazy {
        applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }
    private val isLocationEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    private var binding: ActivityMainBinding? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isGranted: Boolean = false
            for (perm in permissions) {
                isGranted = perm.value
            }
            if (isGranted)
                setContentView(binding?.root)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkForPermission()
        if (isPermissionGranted())
            setContentView(binding?.root)

        requestToEnableLocation()
    }

    private fun isPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun showInContextUI() {
        val dialog = AlertDialog.Builder(applicationContext)
            .setMessage("We need your permission")
            .setNegativeButton("Cancel") {
                _, _ ->
                finish()
            }
            .setPositiveButton("Agree") {
                _, _ ->
                requestPermission()
            }
            .create()
        dialog.show()
    }

    private fun checkForPermission() {
        when {
            isPermissionGranted() -> {
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showInContextUI()
            }

            else -> {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            )
        )
    }

    private fun requestToEnableLocation() {
        if (!isLocationEnabled) {
            val enableLocationDialog = AlertDialog.Builder(applicationContext)
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
