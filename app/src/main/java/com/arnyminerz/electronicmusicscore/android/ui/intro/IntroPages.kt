package com.arnyminerz.electronicmusicscore.android.ui.intro

import android.Manifest
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import com.arnyminerz.electronicmusicscore.android.ui.intro.bt.BtListDevice
import com.arnyminerz.electronicmusicscore.android.ui.intro.bt.BtListDeviceData
import com.arnyminerz.electronicmusicscore.android.ui.intro.data.IntroListData
import com.arnyminerz.electronicmusicscore.android.ui.intro.data.IntroPageData

const val BT_LIST_CALLBACK_CONNECT = 0

const val BT_LIST_CALLBACK_DEVICE_NAME = "device_name"
const val BT_LIST_CALLBACK_DEVICE_MAC = "device_mac"

object IntroPages {
    val PAGE_1 = IntroPageData<Any>(
        "Welcome to ElectronicMusicScore",
        "We are going to guide you through the setup wizard for connecting your phone to the EMS device.",
        "👋🏼",
    )

    val PAGE_2 = IntroPageData<Any>(
        "We need your permission",
        "To search for the devices around you we need that you explicitly give us permission. Please, press the grant option to show the request dialog.",
        "🛡️",
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            listOf(
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        else null,
    )

    @ExperimentalMaterial3Api
    val PAGE_3 = IntroPageData<BtListDeviceData>(
        "Search for your device",
        "Wait for your device to appear on the list and tap it to begin the connection.",
        "🔗",
        listItems = IntroListData(
            IntroListData.Appearance(
                "Start searching",
                "Stop searching",
                Icons.Rounded.Close,
                Icons.Rounded.Search,
            )
        ) { item ->
            BtListDevice(data = item) {
                this.actionCallback.invoke(
                    BT_LIST_CALLBACK_CONNECT,
                    hashMapOf(
                        BT_LIST_CALLBACK_DEVICE_NAME to item.name,
                        BT_LIST_CALLBACK_DEVICE_MAC to item.mac,
                    )
                )
            }
        }
    )
}
