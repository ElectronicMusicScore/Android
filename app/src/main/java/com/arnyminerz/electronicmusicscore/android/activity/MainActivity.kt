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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkWifi
import androidx.compose.material.icons.outlined.NetworkWifi1Bar
import androidx.compose.material.icons.outlined.NetworkWifi2Bar
import androidx.compose.material.icons.outlined.NetworkWifi3Bar
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.electronicmusicscore.android.preferences.Keys
import com.arnyminerz.electronicmusicscore.android.ui.theme.ElectronicMusicScoreTheme
import com.arnyminerz.electronicmusicscore.android.utils.dataStore
import com.arnyminerz.electronicmusicscore.android.utils.doAsync
import com.arnyminerz.electronicmusicscore.android.wireless.ble.BLEService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {
    private val introRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            if (resultCode == IntroActivity.RESULT_CODE_CONNECTED)
                doAsync { performConnection() }
        }

    private var bluetoothService = mutableStateOf<BLEService?>(null)

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            bluetoothService.value =
                (service as BLEService.LocalBinder).getService()
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
            bluetoothService.value = null
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
        val btService = bluetoothService.value
        if (btService == null) {
            Timber.e("Bluetooth service not initialized.")
            return false
        }

        Timber.i("Getting mac address...")
        val mac = runBlocking {
            dataStore.data.map { prefs -> prefs[Keys.DEVICE_MAC] }.first()
        } ?: throw IllegalStateException("There's no stored device mac address to connect to.")

        Timber.i("Connecting to device ($mac)...")
        return btService.connect(mac)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gattServiceIntent = Intent(this, BLEService::class.java)
        startService(gattServiceIntent)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            ElectronicMusicScoreTheme {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        val btService by bluetoothService
                        if (btService != null) {
                            val networks by btService!!.networksInRange
                            LazyColumn {
                                items(networks) { network ->
                                    ListItem(
                                        icon = {
                                            Icon(
                                                if (network.rssi < -80)
                                                    Icons.Outlined.NetworkWifi1Bar
                                                else if (network.rssi < -50)
                                                    Icons.Outlined.NetworkWifi2Bar
                                                else if (network.rssi < -30)
                                                    Icons.Outlined.NetworkWifi3Bar
                                                else
                                                    Icons.Outlined.NetworkWifi,
                                                contentDescription = "Wifi signal",
                                            )
                                        },
                                        trailing = {
                                            IconButton(onClick = { /*TODO*/ }) {
                                                Icon(
                                                    Icons.Rounded.Link,
                                                    "Connect"
                                                )
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = network.ssid,
                                            // color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            // modifier = Modifier
                                            //     .fillMaxWidth(),
                                        )
                                    }
                                }
                            }
                        } else
                            Text(
                                "BLE service not available",
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothService.value?.close()
    }
}