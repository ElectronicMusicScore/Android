package com.arnyminerz.electronicmusicscore.android.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import com.arnyminerz.electronicmusicscore.android.preferences.Keys
import com.arnyminerz.electronicmusicscore.android.preferences.Keys.DEVICE_MAC
import com.arnyminerz.electronicmusicscore.android.ui.intro.BT_LIST_CALLBACK_CONNECT
import com.arnyminerz.electronicmusicscore.android.ui.intro.BT_LIST_CALLBACK_DEVICE_MAC
import com.arnyminerz.electronicmusicscore.android.ui.intro.CALLBACK_CODE_START_LOADING
import com.arnyminerz.electronicmusicscore.android.ui.intro.CALLBACK_CODE_STOP_LOADING
import com.arnyminerz.electronicmusicscore.android.ui.intro.IntroPages
import com.arnyminerz.electronicmusicscore.android.ui.intro.IntroWindow
import com.arnyminerz.electronicmusicscore.android.ui.intro.bt.BtListDeviceData
import com.arnyminerz.electronicmusicscore.android.ui.intro.data.IntroPageData
import com.arnyminerz.electronicmusicscore.android.ui.utils.append
import com.arnyminerz.electronicmusicscore.android.utils.dataStore
import com.arnyminerz.electronicmusicscore.android.utils.doAsync
import com.arnyminerz.electronicmusicscore.android.wireless.ble.BLEService
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import timber.log.Timber

const val BLUETOOTH_SEARCH_TIMEOUT = 100000L

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
)
class IntroActivity : AppCompatActivity() {
    companion object {
        const val RESULT_CODE_CONNECTED = 0
    }

    private lateinit var btManager: BluetoothManager
    private var btAdapter: BluetoothAdapter? = null

    private var bluetoothService: BLEService? = null

    private lateinit var btTimeoutHandler: Handler

    private val foundDevicesList
        get() = page3.listItems?.items

    private val foundBtDeviceReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent?) {
            val action = intent?.action
            if (action != BluetoothDevice.ACTION_FOUND)
                return
            val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
            val deviceName: String = device.name ?: return

            Timber.i("Got new device. Name=$deviceName")
            foundDevicesList?.value = foundDevicesList!!
                .value
                .toMutableList()
                .append(
                    BtListDeviceData(
                        deviceName,
                        device.address,
                        device.address,
                    )
                )
                .toList()
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            bluetoothService = (service as BLEService.LocalBinder).getService()
            bluetoothService?.let { bluetooth ->
                if (!bluetooth.initialize()) {
                    Timber.e("Could not initialize Bluetooth service")
                    finish()
                }
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            bluetoothService = null
        }
    }

    private val page3 = IntroPages.PAGE_3

    private var bleScanning: Boolean
        get() = page3.listItems?.loading?.value ?: false
        set(value) {
            page3.listItems?.loading?.value = value
        }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("Initializing bluetooth timeout handler...")
        btTimeoutHandler = Handler(mainLooper)

        val gattServiceIntent = Intent(this, BLEService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        Timber.d("Getting BluetoothManager...")
        btManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getSystemService(BluetoothManager::class.java)
        else
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        Timber.d("Getting Bluetooth Adapter...")
        btAdapter = btManager.adapter

        if (btAdapter == null) {
            Toast.makeText(this, "Device doesn't support BLE", Toast.LENGTH_LONG).show()
            finishAffinity()
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(foundBtDeviceReceiver, filter)

        page3.listItems?.context?.actionCallback = { code, data ->
            when (code) {
                BT_LIST_CALLBACK_CONNECT -> if (data.containsKey(BT_LIST_CALLBACK_DEVICE_MAC)) {
                    val mac = data.getValue(BT_LIST_CALLBACK_DEVICE_MAC) as String
                    doAsync {
                        Timber.i("Storing device mac...")
                        dataStore.edit { it[DEVICE_MAC] = mac }

                        Timber.i("Mac stored successfully. Returning to main activity.")
                        val intentData = Intent()
                        setResult(RESULT_CODE_CONNECTED, intentData)
                        finish()
                    }
                }
                CALLBACK_CODE_START_LOADING -> {
                    Timber.i("Starting scan.")
                    btAdapter?.startDiscovery()
                    bleScanning = true
                    btTimeoutHandler.postDelayed({
                        btAdapter
                            ?.takeIf { it.isDiscovering }
                            ?.cancelDiscovery()
                        bleScanning = false
                    }, BLUETOOTH_SEARCH_TIMEOUT)
                }
                CALLBACK_CODE_STOP_LOADING -> {
                    Timber.i("Stopping scan.")
                    btAdapter?.cancelDiscovery()
                    bleScanning = false
                }
                else -> Timber.w("Got invalid callback with code $code and data: $data")
            }
        }

        setContent {
            IntroWindow(
                pages = listOf<IntroPageData<*>>(
                    IntroPages.PAGE_1,
                    IntroPages.PAGE_2,
                    page3,
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(foundBtDeviceReceiver)
    }
}
