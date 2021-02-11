/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.util.network

import android.net.ConnectivityManager
import android.net.Network

/**
 * inherits from
 */

abstract class InternetCallback : ConnectivityManager.NetworkCallback() {

    abstract fun onNetworkInActive(network: Network?)

    abstract fun onNetworkActive(network: Network)

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        onNetworkActive(network)
    }

    override fun onUnavailable() {
        super.onUnavailable()
        onNetworkInActive(null)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        onNetworkInActive(network)
    }
}
