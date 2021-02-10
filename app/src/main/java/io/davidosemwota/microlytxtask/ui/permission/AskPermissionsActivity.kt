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
