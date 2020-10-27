package com.example.firebasetextrecognizer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission(var activity: Activity) {
    var isAccessGranted = false

    /**
     * Displaying permission dialog and granting access to external storage
     * @param dialogMsg
     */
    fun grantAccess(dialogMsg: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder(activity)
                        .setTitle("Grant Access")
                        .setMessage(dialogMsg)
                        .setPositiveButton("OK") { dialog, which ->
                            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
                        }
                        .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                        .setCancelable(false)
                        .show()
            } else {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        } else {
            isAccessGranted = true
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100
    }

}