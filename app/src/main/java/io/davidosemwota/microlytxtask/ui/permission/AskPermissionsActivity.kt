/*
 *
 * 2021 David Osemwota.
 */
package io.davidosemwota.microlytxtask.ui.permission

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.davidosemwota.microlytxtask.R
import io.davidosemwota.microlytxtask.databinding.AskPermisionActivityBinding
import io.davidosemwota.microlytxtask.ui.main.MainActivity
import io.davidosemwota.microlytxtask.util.extentions.launchActivity

class AskPermissionsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val isGranted = (false !in permissions.values)
            if (isGranted)
                launchActivity(MainActivity::class.java)
        }

    private lateinit var binding: AskPermisionActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AskPermisionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPermissionAsk()

        binding.askPermissionBtn.setOnClickListener {
            initPermissionAsk()
        }
    }

    private fun showInContextUI() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.permission_ask_msg))
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

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
            )
        )
    }

    private fun initPermissionAsk() {
        when {

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
}
