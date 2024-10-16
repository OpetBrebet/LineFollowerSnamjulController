package com.brebet.linefollowersnamjulcontroller.viewmodel

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brebet.linefollowersnamjulcontroller.data.bluetooth.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    private val controller: BluetoothController
) : ViewModel() {
    private lateinit var selectedDevice: BluetoothDevice

    private val _motorSpeed = MutableStateFlow(0)
    val motorSpeed = _motorSpeed.asStateFlow()

    private val _motorSpeedBias = MutableStateFlow(0)
    val motorSpeedBias = _motorSpeedBias.asStateFlow()

    fun connectToDevice(_selectedDevice: BluetoothDevice) {
        selectedDevice = _selectedDevice
        attemptConnection()
    }

    private fun attemptConnection() {
        viewModelScope.launch {
            val flow = controller.connect(selectedDevice)
            flow.collectLatest { result ->
                when (result) {
                    is BluetoothController.ConnectionResult.Established -> {
                        refreshDataFromDevice()
                    }
                    is BluetoothController.ConnectionResult.Error -> {
                        Log.d("Bluetooth", "Connection error. Retrying...")
                        attemptConnection()
                    }
                }
            }
        }
    }

    fun refreshDataFromDevice() {
        viewModelScope.launch {
            controller.sendData("r".toByteArray())
            controller.receivedData.collectLatest { byteArray ->
                parseReceivedData(byteArray)
            }
        }
    }

    private fun parseReceivedData(data: ByteArray) {
        if (data.isNotEmpty()) {
            var i = 0
            while (i < data.size) {
                when (data[i].toChar()) {
                    's' -> { // s: Motor Speed
                        if (i + 1 < data.size) {
                            val speed = data[i + 1].toUByte().toInt()
                            _motorSpeed.value = speed
                            i += 2
                        }
                    }
                    'b' -> { // b: Motor Speed Bias
                        if (i + 1 < data.size) {
                            val speedBias = data[i + 1].toUByte().toInt() - 127
                            _motorSpeedBias.value = speedBias
                            i += 2
                        }
                    }
                    else -> i++ // Next byte if no match
                }
            }
        }
    }

    fun sendStart() {
        val byteArray = "t".toByteArray() + 0x73
        controller.sendData(byteArray)
    }
    fun sendStop() {
        val byteArray = "p".toByteArray() + 0x73
        controller.sendData(byteArray)
            }
    fun sendSpeed(speed: Int) {
        val byteArray = "a".toByteArray() + speed.toByte()
        controller.sendData(byteArray)
    }
    fun sendSpeedBias(speedBias: Int) {
        val byteArray = "b".toByteArray() + (speedBias + 127).toByte()
        controller.sendData(byteArray)
    }
}