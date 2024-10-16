package com.brebet.linefollowersnamjulcontroller.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brebet.linefollowersnamjulcontroller.data.bluetooth.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectDeviceViewModel @Inject constructor(
    private val controller: BluetoothController,
) : ViewModel() {

    var failText: String? by mutableStateOf(null)
        private set
    var pairedDevices: List<BluetoothDevice> by mutableStateOf(emptyList())
        private set

    init {
        observeFailString()
        observePairedDevices()
    }

    private fun observeFailString() {
        viewModelScope.launch {
            controller.failString.collectLatest { failString ->
                failText = failString
            }
        }
    }

    private fun observePairedDevices() {
        viewModelScope.launch {
            controller.pairedDevices.collectLatest { devices ->
                pairedDevices = devices
            }
        }
    }

    fun onRefresh() {
        controller.refreshBluetooth()
    }
}