package io.davidosemwota.microlytxtask

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.davidosemwota.microlytxtask.ui.main.MainActivity
import io.davidosemwota.microlytxtask.ui.permission.AskPermissionsActivity
import io.davidosemwota.microlytxtask.util.extentions.isPermissionGranted
import io.davidosemwota.microlytxtask.util.extentions.launchActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isPermissionGranted(applicationContext)){
            launchActivity(MainActivity::class.java)
        }else {
            launchActivity(AskPermissionsActivity::class.java)
        }

    }

}