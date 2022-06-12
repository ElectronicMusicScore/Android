package com.arnyminerz.electronicmusicscore.android.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import com.arnyminerz.electronicmusicscore.android.preferences.Keys
import com.arnyminerz.electronicmusicscore.android.ui.theme.ElectronicMusicScoreTheme
import com.arnyminerz.electronicmusicscore.android.utils.dataStore
import com.arnyminerz.electronicmusicscore.android.utils.doAsync
import com.arnyminerz.electronicmusicscore.android.wireless.ble.BLEService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val introRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            if (resultCode == IntroActivity.RESULT_CODE_CONNECTED)
                doAsync { performConnection() }
        }

    private var bluetoothService: BLEService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            bluetoothService = (service as BLEService.LocalBinder).getService()
                .also {
                    if (!it.initialize()) {
                        Timber.e("Could not initialize Bluetooth service")
                        finish()
                    } else Timber.i("Bluetooth service initialized successfully.")
                }
            try {
                performConnection()
            } catch (e: IllegalStateException) {
                introRequest.launch(Intent(this@MainActivity, IntroActivity::class.java))
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName?) {
            bluetoothService = null
        }
    }

    /**
     * Tries to connect to an stored mac address. Used [bluetoothService] to do so.
     * @author Arnau Mora
     * @since 20220609
     * @throws IllegalStateException When there isn't any stored mac address.
     * @return false when [bluetoothService] is null, or [BLEService.connect] returned false. true
     * otherwise.
     */
    @Throws(IllegalStateException::class)
    private fun performConnection(): Boolean {
        if (bluetoothService == null) {
            Timber.e("Bluetooth service not initialized.")
            return false
        }

        Timber.i("Getting mac address...")
        val mac = runBlocking {
            dataStore.data.map { prefs -> prefs[Keys.DEVICE_MAC] }.first()
        } ?: throw IllegalStateException("There's no stored device mac address to connect to.")

        Timber.i("Connecting to device ($mac)...")
        return bluetoothService?.connect(mac) ?: return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gattServiceIntent = Intent(this, BLEService::class.java)
        startService(gattServiceIntent)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            ElectronicMusicScoreTheme {
                Text("Hello world!")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService?.close()
    }
}