package com.brebet.linefollowersnamjulcontroller.screen

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brebet.linefollowersnamjulcontroller.viewmodel.SelectDeviceViewModel

@SuppressLint("MissingPermission")
@Composable
fun SelectDeviceScreen(
    viewmodel: SelectDeviceViewModel = hiltViewModel(),
    onNextButtonClicked: (device: BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            items(
                viewmodel.pairedDevices
            ) { device ->
                Text(
                    text = device.name ?: (device.address + "(No Name)"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNextButtonClicked(device)
                        }
                        .padding(16.dp)
                )
            }
        }

        if (viewmodel.failText != null) {
            Text(
                text = viewmodel.failText!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(
                onClick = viewmodel::onRefresh
            ) {
                Text(
                    text = "Refresh"
                )
            }
        }
    }
}