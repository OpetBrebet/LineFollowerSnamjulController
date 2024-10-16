package com.brebet.linefollowersnamjulcontroller.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.brebet.linefollowersnamjulcontroller.util.PermissionUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothControllerImpl(
    private val context: Context,
    private val adapter: BluetoothAdapter?,
) : BluetoothController {

    companion object {
        private val BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val _pairedDevices = MutableStateFlow(emptyList<BluetoothDevice>())
    override val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()

    private val _failString = MutableSharedFlow<String?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val failString: Flow<String?> =_failString.distinctUntilChanged()

    private val _receivedData = MutableSharedFlow<ByteArray>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val receivedData: Flow<ByteArray> = _receivedData


    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var service: BluetoothDataTransferService? = null
    private var socket: BluetoothSocket? = null
    private var getPairedDevicesJob: Job? = null

    init {
        refreshBluetooth()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getPairedDevices() {
        if (adapter == null) {
            _failString.emit("Bluetooth is not supported")
            _pairedDevices.emit(emptyList())
            return
        }
        else if (!adapter.isEnabled) {
            _failString.emit("Bluetooth is not enabled")
            _pairedDevices.emit(emptyList())
            return
        }
        else if (!PermissionUtil.canBluetoothConnect(context)) {
            _failString.emit("No permission for bluetooth. Try reinstalling?")
            _pairedDevices.emit(emptyList())
            return
        }

        val devices = ArrayList<BluetoothDevice>()
        for (device: BluetoothDevice in adapter.getBondedDevices()!!)
            if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE)
                devices.add(device)
        if (devices.isNotEmpty()) {
            _failString.emit(null)
            _pairedDevices.emit(devices)
        }
        else
            _failString.emit("No bluetooth device found")
    }

    //API

    override fun bluetoothEnabled(): Boolean {
        return adapter?.isEnabled == true
    }

    override fun refreshBluetooth() {
        getPairedDevicesJob?.cancel()
        getPairedDevicesJob = scope.launch {
            getPairedDevices()
        }
    }

    override fun connect(device: BluetoothDevice): Flow<BluetoothController.ConnectionResult> {
        return flow {
            try {
                socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP)
                adapter?.cancelDiscovery()
                socket?.connect()
                emit(BluetoothController.ConnectionResult.Established)

                if (socket != null) {
                    service = BluetoothDataTransferService(socket!!, _receivedData)
                    service?.listenData()
                }
            } catch (e: IOException) {
                emit(BluetoothController.ConnectionResult.Error("Connection was interrupted!"))
                e.printStackTrace()
                try {
                    socket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun close() {
        socket?.close()
        socket = null
    }

    override fun sendData(data: ByteArray): Boolean {
        if (!PermissionUtil.canBluetoothConnect(context))
            return false
        if (service == null)
            return false

        val success = service?.sendData(data) == true
        return success
    }

    class BluetoothDataTransferService(
        private val socket: BluetoothSocket,
        private val receivedDataFlow: MutableSharedFlow<ByteArray>
    ) {
        fun sendData(data: ByteArray): Boolean {
            try {
                socket.outputStream.write(data)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        fun listenData() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val buffer = ByteArray(1024)
                    val inputStream = socket.inputStream

                    while (true) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead > 0) {
                            receivedDataFlow.emit(buffer)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}