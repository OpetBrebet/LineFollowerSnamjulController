package com.brebet.linefollowersnamjulcontroller.di.module

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import com.brebet.linefollowersnamjulcontroller.data.bluetooth.BluetoothController
import com.brebet.linefollowersnamjulcontroller.data.bluetooth.BluetoothControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideBluetoothManager(application: Application): BluetoothManager = application.getSystemService(
        BluetoothManager::class.java)

    @Provides
    @Singleton
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter? = bluetoothManager.adapter

    @Provides
    @Singleton
    fun provideAndroidBluetoothController(
        application: Application,
        bluetoothAdapter: BluetoothAdapter?,
    ): BluetoothController = BluetoothControllerImpl(
        context = application,
        adapter = bluetoothAdapter
    )
}