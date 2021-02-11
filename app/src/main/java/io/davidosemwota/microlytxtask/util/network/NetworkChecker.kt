/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

object NetworkChecker {

    fun registerInternetAvailabilityCallback(context: Context, callback: InternetCallback) {

        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .build()

            val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectManager.registerNetworkCallback(request, callback)
        } catch (ex: Exception) {
        }
    }

    fun unRegisterInternetAvailabilityCallback(context: Context, callback: InternetCallback) {

        try {

            val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectManager.unregisterNetworkCallback(callback)
        } catch (ex: Exception) {
        }
    }
}
