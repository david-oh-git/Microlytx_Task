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
package io.davidosemwota.microlytxtask.ui.home

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.davidosemwota.microlytxtask.R
import io.davidosemwota.microlytxtask.data.DataSource
import io.davidosemwota.microlytxtask.data.PhoneDetail
import io.davidosemwota.microlytxtask.databinding.FragmentHomeBinding
import io.davidosemwota.microlytxtask.ui.extentions.observe
import io.davidosemwota.microlytxtask.ui.extentions.setItemDecorationSpacing
import io.davidosemwota.microlytxtask.ui.home.adaptor.PhoneDetailAdaptor
import io.davidosemwota.microlytxtask.util.extentions.isPermissionGranted
import io.davidosemwota.microlytxtask.util.extentions.logd
import io.davidosemwota.microlytxtask.util.network.InternetCallback
import io.davidosemwota.microlytxtask.util.network.NetworkChecker

class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()
    private val phoneDetailAdaptor by lazy { PhoneDetailAdaptor() }
    private lateinit var locationManager: LocationManager
    private var callback: InternetCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = requireContext().getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        initInternetCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.listOfPhoneDetails, ::onDataChange)
        setUpViews()
    }

    override fun onResume() {
        super.onResume()

        callback?.let {
            NetworkChecker.registerInternetAvailabilityCallback(requireContext(), it)
        }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG, "onPause: yes ")

        callback?.let {
            NetworkChecker.unRegisterInternetAvailabilityCallback(requireContext(), it)
        }
    }

    override fun onStart() {
        super.onStart()

        logd(TAG, "onStart: ")
    }

    private fun setUpViews() {
        setUpRecyclerView()

        val tm = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        reloadData(tm)

        if (isPermissionGranted(requireContext())) {
            logd("HomeFragment", "permission granted")
            val cellLocation = tm.allCellInfo

            for (info in cellLocation) {
                when (info) {
                    is CellInfoGsm -> {
                        val identityGsm = info.cellIdentity
                        val lac = identityGsm.lac
                        val cid = identityGsm.cid

                        if (info.isRegistered) {
                            viewModel.addPhoneDetail(
                                PhoneDetail("Local Area Code (LAC)", lac.toString())
                            )

                            viewModel.addPhoneDetail(
                                PhoneDetail("Cell Identity (CID)", cid.toString())
                            )
                        }
                    }

                    is CellInfoLte -> {
                        val lac = info.cellIdentity.ci
                        logd(TAG, "cell id $lac")
                        viewModel.addPhoneDetail(
                            PhoneDetail("Cell ID (CI)", lac.toString())
                        )
                    }

                    else -> {
                        logd("HomeFragment", "nothing matched")
                    }
                }
            }
        } else {
            logd("HomeFragment", "permission not granted")
        }
    }

    private fun reloadData(tm: TelephonyManager) {
        if (isPermissionGranted(requireContext())) {
            DataSource.initialise(tm, viewModel, locationManager, requireContext())
        }
    }

    private fun onDataChange(data: List<PhoneDetail>) {
        phoneDetailAdaptor.submitList(data)
    }

    private fun setUpRecyclerView() {
        binding.includePhoneDetailList.phoneDetailList.apply {
            this.adapter = phoneDetailAdaptor
            setItemDecorationSpacing(
                resources.getDimension(R.dimen.view_phone_detail_list_item_padding)
            )
        }
    }

    private fun checkNetworkType() {
        val telephonyManager = requireContext().getSystemService(
            Context.TELEPHONY_SERVICE
        ) as TelephonyManager

        val connectivityManager = requireContext().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
//        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

    private fun initInternetCallback() {

        callback = object : InternetCallback() {
            override fun onNetworkInActive(network: Network?) {
                viewModel.addPhoneDetail(
                    PhoneDetail("Cell Connection Status", "Not Connected"),
                    true
                )
            }

            override fun onNetworkActive(network: Network) {
                viewModel.addPhoneDetail(
                    PhoneDetail("Cell Connection Status", "Connected"),
                    true
                )
            }
        }
    }
}
