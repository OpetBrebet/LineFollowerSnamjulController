package com.brebet.linefollowersnamjulcontroller.data.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val pairedDevices: StateFlow<List<BluetoothDevice>>
    val failString: Flow<String?>
    val receivedData: Flow<ByteArray>

    fun bluetoothEnabled(): Boolean
    fun refreshBluetooth()
    fun connect(device: BluetoothDevice): Flow<ConnectionResult>
    fun close()
    fun sendData(data: ByteArray): Boolean

    sealed interface ConnectionResult {
        data object Established : ConnectionResult
        data class Error(val message: String) : ConnectionResult
    }
}