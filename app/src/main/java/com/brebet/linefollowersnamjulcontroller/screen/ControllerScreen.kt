package com.brebet.linefollowersnamjulcontroller.screen

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brebet.linefollowersnamjulcontroller.viewmodel.ControllerViewModel
import kotlin.math.roundToInt

@Composable
fun ControllerScreen(
    viewModel: ControllerViewModel = hiltViewModel(),
    selectedDevice: BluetoothDevice,
    modifier: Modifier = Modifier,
) {
    val motorSpeedState  = viewModel.motorSpeed.collectAsState()
    val motorSpeedBiasState = viewModel.motorSpeedBias.collectAsState()

    var sliderSpeedPosition = remember { mutableFloatStateOf(motorSpeedState.value.toFloat()) }
    var textFieldSpeedPosition = remember { mutableStateOf(motorSpeedState.value.toString()) }

    var sliderSpeedBiasPosition = remember { mutableFloatStateOf(motorSpeedBiasState.value.toFloat()) }
    var textFieldSpeedBiasPosition = remember { mutableStateOf(motorSpeedBiasState.value.toString()) }

    viewModel.connectToDevice(selectedDevice)

    Scaffold(
        modifier = modifier,
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding).padding(
                    horizontal = 10.dp
                ),
                content = {
                    Row(
                        content = {
                            Button(
                                onClick = { viewModel.sendStart() }
                            ) {
                                Text(text = "Start")
                            }
                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )
                            Button(
                                onClick = { viewModel.sendStop() }
                            ) {
                                Text(text = "Stop")
                            }
                        }
                    )
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // Speed
                    Row(
                        content = {
                            Column(
                                content = {
                                    Text(text = "Speed Value")
                                    Slider(
                                        value = sliderSpeedPosition.floatValue,
                                        onValueChange = { value: Float ->
                                            sliderSpeedPosition.floatValue = value
                                            textFieldSpeedPosition.value = value.roundToInt().toString()
                                            viewModel.sendSpeed(value.roundToInt())
                                        },
                                        valueRange = 0f..255f,
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    )
                                }
                            )
                            TextField(
                                value = textFieldSpeedPosition.value,
                                onValueChange = { input: String ->
                                    textFieldSpeedPosition.value = input
                                    val inputFloat = input.toFloatOrNull()

                                    if (inputFloat != null) {
                                        sliderSpeedPosition.floatValue = inputFloat.coerceIn(0f..255f)
                                        viewModel.sendSpeed(inputFloat.roundToInt())
                                    }
                                },
                                label = { Text(text = "Value") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true
                            )
                        }
                    )
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // Speed Bias
                    Row(
                        content = {
                            Column(
                                content = {
                                    Text(text = "Speed Bias Value")
                                    Slider(
                                        value = sliderSpeedBiasPosition.floatValue,
                                        onValueChange = { value: Float ->
                                            sliderSpeedBiasPosition.floatValue = value
                                            textFieldSpeedBiasPosition.value = value.roundToInt().toString()
                                            viewModel.sendSpeedBias(value.roundToInt())
                                        },
                                        valueRange = -127f..128f,
                                        modifier = Modifier.fillMaxWidth(0.8f)
                                    )
                                }
                            )
                            TextField(
                                value = textFieldSpeedBiasPosition.value,
                                onValueChange = { input: String ->
                                    textFieldSpeedBiasPosition.value = input
                                    val inputFloat = input.toFloatOrNull()

                                    if (inputFloat != null) {
                                        sliderSpeedBiasPosition.floatValue = inputFloat.coerceIn(-127f..128f)
                                        viewModel.sendSpeedBias(inputFloat.roundToInt())
                                    }
                                },
                                label = { Text(text = "Value") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true
                            )
                        }
                    )
                }
            )
        }
    )

    LaunchedEffect(motorSpeedState.value) {
        sliderSpeedPosition.floatValue = motorSpeedState.value.toFloat()
        textFieldSpeedPosition.value = motorSpeedState.value.toString()
    }
    LaunchedEffect(motorSpeedBiasState.value) {
        sliderSpeedBiasPosition.floatValue = motorSpeedBiasState.value.toFloat()
        textFieldSpeedBiasPosition.value = motorSpeedBiasState.value.toString()
    }
}
