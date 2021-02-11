/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.util.extentions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import io.davidosemwota.microlytxtask.BuildConfig

fun logd(tag: String, msg: String) {
    if (BuildConfig.DEBUG)
        Log.d(tag, msg)
}

/**
 * Check if permissions have been granted.
 */
fun isPermissionGranted(applicationContext: Context): Boolean =
    ContextCompat.checkSelfPermission(
        applicationContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
        applicationContext,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

/**
 * To launch an activity.
 */
fun <T> AppCompatActivity.launchActivity(activity: Class<T>) {
    startActivity(Intent(this, activity))
    finish()
}
