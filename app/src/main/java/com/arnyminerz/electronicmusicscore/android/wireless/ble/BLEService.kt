package com.arnyminerz.electronicmusicscore.android.wireless.ble

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import timber.log.Timber

/**
 * Makes manipulating BLE connections easier, through accessible functions, and intent broadcasts.
 *
 * Broadcasts:
 * * [ACTION_GATT_CONNECTED]: Gets called when a new connection has been established.
 * * [ACTION_GATT_DISCONNECTED]: Gets called when a device gets disconnected.
 *
 * [initialize] must be called before using any of the other functions.
 * @author Arnau Mora
 * @since 20220609
 * @see LocalBinder
 * @see initialize
 */
class BLEService : Service() {
    companion object {
        /**
         * Gets broadcast by the service when a BLE device gets connected.
         * @author Arnau Mora
         * @since 20220609
         */
        const val ACTION_GATT_CONNECTED =
            "com.arnyminerz.electronicmusicscore.android.bluetooth.le.ACTION_GATT_CONNECTED"

        /**
         * Gets broadcast by the service when a BLE device gets disconnected.
         * @author Arnau Mora
         * @since 20220609
         */
        const val ACTION_GATT_DISCONNECTED =
            "com.arnyminerz.electronicmusicscore.android.bluetooth.le.ACTION_GATT_DISCONNECTED"

        /**
         * Gets broadcast by the service when a service gets discovered in a connected BLE device.
         * @author Arnau Mora
         * @since 20220609
         */
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.arnyminerz.electronicmusicscore.android.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"

        const val BROADCAST_INTENT_EXTRA_MAC_KEY = "mac_address"
    }

    /**
     * The binder instance for binding the service.
     * @author Arnau Mora
     * @since 20220609
     */
    private val binder = LocalBinder()

    /**
     * The bluetooth adapter used for controlling the device's Bluetooth connections. Gets
     * initialized by [initialize].
     * @author Arnau Mora
     * @since 20220609
     */
    private var bluetoothAdapter: BluetoothAdapter? = null

    /**
     * When a connection is established with a BLE device, it gets stored here.
     * @author Arnau Mora
     * @since 20220609
     * @see connect
     */
    private var bluetoothGatt: BluetoothGatt? = null

    /**
     * A callback that gets updated with the connection status of a request made to [connect].
     * @author Arnau Mora
     * @since 20220609
     * @see connect
     */
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    broadcastUpdate(
                        ACTION_GATT_CONNECTED,
                        Bundle().apply {
                            gatt?.let {
                                putString(
                                    BROADCAST_INTENT_EXTRA_MAC_KEY,
                                    it.device.address
                                )
                            }
                        }
                    )
                    bluetoothGatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS)
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            else
                Timber.w("Discovered services. Status not successful: $status")
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    /**
     * Closes the current connection if any.
     * @author Arnau Mora
     * @since 20220609
     */
    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    /**
     * Initializes the service. Must be called before using any of the other functions.
     * @author Arnau Mora
     * @since 20220609
     * @return True if the service was initialized successfully, false if an error has occurred
     * while doing so. Errors get logged.
     */
    fun initialize(): Boolean {
        Timber.d("Getting bluetooth manager...")
        val bluetoothManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getSystemService(BluetoothManager::class.java)
        else
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        Timber.d("Getting bluetooth adapter...")
        bluetoothAdapter = bluetoothManager.adapter

        return if (bluetoothAdapter == null) {
            Timber.e("Could not obtain a Bluetooth Adapter. Device may be incompatible.")
            false
        } else {
            Timber.i("BLEService initialized correctly.")
            true
        }
    }

    /**
     * Connects to a device with a MAC address. Broadcasts the result of the connection with action
     * [ACTION_GATT_CONNECTED] and [ACTION_GATT_DISCONNECTED].
     * @author Arnau Mora
     * @since 20220609
     * @param address The MAC address of the device to connect to.
     * @return True if the connection was performed correctly, false otherwise.
     */
    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            Timber.e("Address provided for connecting is invalid.")
            return false
        }
        return bluetoothAdapter?.let { adapter ->
            try {
                Timber.d("Getting remote device at $address...")
                val device = adapter.getRemoteDevice(address)
                Timber.d("Connecting to the remote device...")
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                true
            } catch (e: IllegalArgumentException) {
                Timber.e("Device not found with the provided address.")
                false
            }
        } ?: run {
            Timber.e("Bluetooth Adapter not initialized.")
            false
        }
    }

    /**
     * Returns all the available Gatt services on the currently connected device. May only be
     * called after receiving [ACTION_GATT_SERVICES_DISCOVERED].
     * @author Arnau Mora
     * @since 20220609
     * @return A list of the available services or null if currently not connected to any device.
     */
    fun getSupportedGattServices(): List<BluetoothGattService>? = bluetoothGatt?.services

    /**
     * Used for broadcasting something when an update has been detected.
     * @author Arnau Mora
     * @since 20220609
     * @param action The name of the action to broadcast.
     * @param extras Extra data to pass to the broadcast intent.
     * @see ACTION_GATT_CONNECTED
     * @see ACTION_GATT_DISCONNECTED
     */
    private fun broadcastUpdate(action: String, extras: Bundle = Bundle()) {
        val intent = Intent(action).apply {
            putExtras(extras)
        }
        sendBroadcast(intent)
    }

    /**
     * Closes the current BLE connection if any.
     * @author Arnau Mora
     * @since 20220609
     */
    @SuppressLint("MissingPermission")
    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }

    /**
     * Used for binding the Service.
     * @author Arnau Mora
     * @since 20220609
     */
    inner class LocalBinder : Binder() {
        /**
         * Returns the [BLEService] instance associated with the Binder.
         * @author Arnau Mora
         * @since 20220609
         */
        fun getService(): BLEService = this@BLEService
    }
}