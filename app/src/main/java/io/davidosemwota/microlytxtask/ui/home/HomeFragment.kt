/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.home

import android.content.Context
import android.location.LocationManager
import android.net.Network
import android.os.Bundle
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
