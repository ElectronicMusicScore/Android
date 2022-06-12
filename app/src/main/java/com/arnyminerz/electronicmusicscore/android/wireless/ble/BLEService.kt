package com.arnyminerz.electronicmusicscore.android.wireless.ble

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import com.arnyminerz.electronicmusicscore.android.wireless.ble.BLEService.LocalBinder
import com.arnyminerz.electronicmusicscore.android.wireless.wifi.ScanNetworkData
import timber.log.Timber
import java.util.UUID

val DEVICE_DATA_SERVICE_UUID: UUID = UUID.fromString("0000181c-0000-1000-8000-00805f9b34fb")

val WIFI_SCAN_CHARACTERISTIC_UUID: UUID = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb")

/**
 * Makes manipulating BLE connections easier, through accessible functions.
 *
 * [initialize] must be called before using any of the other functions.
 * @author Arnau Mora
 * @since 20220609
 * @see LocalBinder
 * @see initialize
 */
class BLEService : Service() {
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

    private val clientManagers = hashMapOf<String, ClientManager>()

    private lateinit var bleHandler: Handler

    private var device: BluetoothDevice? = null

    val networksInRange = mutableStateOf(emptyList<ScanNetworkData>())

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

        @Suppress("DEPRECATION")
        bleHandler = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            Handler.createAsync(mainLooper)
        else Handler()

        return if (bluetoothAdapter == null) {
            Timber.e("Could not obtain a Bluetooth Adapter. Device may be incompatible.")
            false
        } else {
            Timber.i("BLEService initialized correctly.")
            true
        }
    }

    /**
     * Connects to a device with a MAC address.
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

        if (bluetoothAdapter == null) {
            Timber.e("Bluetooth Adapter not initialized.")
            return false
        }

        Timber.d("Getting remote device at $address...")
        device = bluetoothAdapter!!.getRemoteDevice(address) ?: run {
            Timber.e("Could not connect to the device at $address.")
            null
        }

        val cm = ClientManager()
        cm.connect(device!!)
            .retry(3, 100)
            .timeout(15_000)
            .useAutoConnect(false)
            .done {
                Timber.i("Connected successfully to device. Scanning wifi...")
                cm.scanWifiNetworks()
            }
            .fail { device, status ->
                Timber.e("Could not connect to $device. Status: $status")
            }
            .enqueue()
        clientManagers[device!!.address] = cm
        return true
    }

    /**
     * Closes the current BLE connection if any.
     * @author Arnau Mora
     * @since 20220609
     */
    @SuppressLint("MissingPermission")
    fun close() {
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

    private inner class ClientManager : no.nordicsemi.android.ble.BleManager(this) {
        private var wifiScanCharacteristic: BluetoothGattCharacteristic? = null

        override fun getGattCallback(): BleManagerGattCallback = BleCallbackImpl()

        override fun log(priority: Int, message: String) {
            Timber.log(priority, message)
        }

        fun scanWifiNetworks() {
            readCharacteristic(wifiScanCharacteristic)
                .done {
                    Timber.i("Received data for wifi scan.")
                }
                .fail { device, status ->
                    Timber.e("Could not scan for wifi networks from $device. Status: $status")
                }
                .with { _, data ->
                    val dataValue = data.value ?: return@with
                    val wList = String(dataValue)
                        .let { it.substring(0, it.lastIndexOf((0x19).toChar())) }
                        .split((0x19).toChar())
                        .map { it.split((0x0A).toChar()) }
                    networksInRange.value = wList
                        .map { ScanNetworkData(it[0], it[1].toShort()) }
                    Timber.i("Got data for wifi scan: $wList")
                }
                .enqueue()
        }

        private inner class BleCallbackImpl : BleManagerGattCallback() {
            override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean =
                gatt.getService(DEVICE_DATA_SERVICE_UUID)
                    ?.getCharacteristic(WIFI_SCAN_CHARACTERISTIC_UUID)
                    ?.also { wifiScanCharacteristic = it } != null

            override fun onServicesInvalidated() {
                wifiScanCharacteristic = null
            }

        }
    }
}