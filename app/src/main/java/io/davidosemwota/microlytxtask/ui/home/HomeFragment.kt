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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.CellInfoGsm
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.davidosemwota.microlytxtask.R
import io.davidosemwota.microlytxtask.data.PhoneDetail
import io.davidosemwota.microlytxtask.databinding.FragmentHomeBinding
import io.davidosemwota.microlytxtask.ui.extentions.observe
import io.davidosemwota.microlytxtask.ui.extentions.setItemDecorationSpacing
import io.davidosemwota.microlytxtask.ui.home.adaptor.PhoneDetailAdaptor

class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted app workflow continues.
            } else {
                //
            }
        }

    private val viewModel: HomeViewModel by viewModels()
    private val phoneDetailAdaptor by lazy { PhoneDetailAdaptor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkForPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.listOfPhoneDetails, ::onDataChange)
        setUpViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun setUpViews() {
        setUpRecyclerView()

        val tm = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mobileCountryCode = tm.networkCountryIso
        val mobileNetworkCode = tm.networkOperator
        val networkOperator = tm.simOperatorName
        viewModel.addPhoneDetail(
            PhoneDetail("Mobile Country Code (MCC)", mobileCountryCode)
        )
        viewModel.addPhoneDetail(
            PhoneDetail("Mobile Network Code (MNC)", mobileNetworkCode)
        )
        viewModel.addPhoneDetail(
            PhoneDetail("Operator Name", networkOperator)
        )
        viewModel.addPhoneDetail(
            PhoneDetail("Handset Make", Build.MANUFACTURER)
        )
        viewModel.addPhoneDetail(
            PhoneDetail("Item Model", Build.MODEL)
        )

//        val simOperator = tm.simOperator
//        val simCountryIso = tm.simCountryIso

        if (isPermissionGranted()) {
            Log.d("HomeFragment", "permission granted")
            val cellLocation = tm.allCellInfo

            for (info in cellLocation) {
                when (info) {
                    is CellInfoGsm -> {
                        val identityGsm = info.cellIdentity
                        val lac = identityGsm.lac
                        viewModel.addPhoneDetail(
                            PhoneDetail("LAC", lac.toString())
                        )
                    }

                    else -> {
                        Log.d("HomeFragment", "nothing matched")
                    }
                }
            }
        } else {
            Log.d("HomeFragment", "permission not granted")
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun isPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext().applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkForPermission() {
        when {
            isPermissionGranted() -> {
            }

            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showInContextUI()
            }

            else -> {
                requestPermission()
            }
        }
    }

    private fun showInContextUI() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("We need your permission")
            .setNegativeButton("Cancel") {
                _, _ ->
                requireActivity().finish()
            }
            .setPositiveButton("Agree") {
                _, _ ->
                requestPermission()
            }
            .create()
        dialog.show()
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
}
