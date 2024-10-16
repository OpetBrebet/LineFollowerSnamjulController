package com.brebet.linefollowersnamjulcontroller.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtil {
    fun canBluetoothConnect(context: Context): Boolean {
        if (BuildUtil.deviceAtLeastS()) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }
}