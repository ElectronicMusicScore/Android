package com.arnyminerz.electronicmusicscore.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.PermDeviceInformation
import androidx.compose.material.icons.rounded.SdCard
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Web
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arnyminerz.electronicmusicscore.android.R
import com.arnyminerz.electronicmusicscore.android.ui.data.IconButtonData
import com.arnyminerz.electronicmusicscore.android.ui.elements.CardPanel
import com.arnyminerz.electronicmusicscore.android.ui.elements.CardPanelRow
import com.arnyminerz.electronicmusicscore.android.ui.elements.ToggleButton
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterial3Api
fun MainScreenContent(drawerToggled: () -> Unit) {
    var showingWifiDialog by remember { mutableStateOf(false) }

    if (showingWifiDialog)
        AlertDialog(
            onDismissRequest = { showingWifiDialog = false },
            confirmButton = {
                TextButton(onClick = { showingWifiDialog = false }) {
                    Text("Close")
                }
            }
        )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                title = {
                    Text(
                        stringResource(R.string.screen_title_main)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = drawerToggled) {
                        Icon(
                            Icons.Rounded.Menu,
                            stringResource(R.string.button_desc_menu),
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            CardPanel(
                "Device information",
                Icons.Rounded.PermDeviceInformation,
                modifier = Modifier.padding(8.dp)
            ) {
                CardPanelRow(
                    title = "Name",
                    message = "Trumpet1",
                    action = IconButtonData(
                        icon = Icons.Rounded.Edit,
                        description = "Rename device",
                    ) {},
                )
                CardPanelRow(
                    title = "Battery level",
                    message = "72%",
                )

                CardPanelRow(
                    title = "Used storage (Internal memory)",
                    message = "165.9 KiB / 987.3 KiB",
                )
                CardPanelRow(
                    title = "Used storage (Memory memory)",
                    message = "2.9 MiB / 15.8 GiB",
                )

                CardPanelRow(
                    title = "WiFi Network",
                    message = "Not connected",
                    action = IconButtonData(
                        icon = Icons.Rounded.Wifi,
                        description = "Connect Wifi",
                    ) { showingWifiDialog = true },
                )
            }
            CardPanel(
                "Device Storage",
                Icons.Rounded.Folder,
                modifier = Modifier.padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    var internalSelected by remember { mutableStateOf(true) }

                    ToggleButton(
                        internalSelected,
                        { internalSelected = true },
                        Icons.Rounded.Storage,
                        "Internal",
                    )
                    ToggleButton(
                        !internalSelected,
                        { internalSelected = false },
                        Icons.Rounded.SdCard,
                        "SD Card",
                    )
                    FilledTonalIconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .fillMaxWidth(.2f),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    ) {
                        Icon(Icons.Rounded.Upload, "Upload")
                    }
                }

                CardPanelRow(
                    title = "Example Score.mxml",
                    message = "16.5 KiB",
                    action = IconButtonData(
                        Icons.Rounded.Delete,
                        "Delete"
                    ) {},
                )
                CardPanelRow(
                    title = "Another Example Score.mxml",
                    message = "24.6 KiB",
                    action = IconButtonData(
                        Icons.Rounded.Delete,
                        "Delete"
                    ) {},
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
@ExperimentalMaterial3Api
fun MainScreenDrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Text(
            text = "Application",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 12.dp, start = 16.dp)
        )

        NavigationDrawerItem(
            selected = true,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "Home")
            },
            icon = {
                Icon(Icons.Rounded.Home, "Home")
            },
        )
        NavigationDrawerItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "Devices")
            },
            icon = {
                Icon(Icons.Rounded.Devices, "Devices")
            },
        )
        NavigationDrawerItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "Settings")
            },
            icon = {
                Icon(Icons.Rounded.Settings, "Settings")
            },
        )

        Divider()

        Text(
            text = "Application",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp, start = 16.dp)
        )
        NavigationDrawerItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "Github")
            },
            icon = {
                Icon(
                    FontAwesomeIcons.Brands.Github,
                    "Settings",
                    modifier = Modifier.size(24.dp)
                )
            },
        )
        NavigationDrawerItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "App Information")
            },
            icon = {
                Icon(Icons.Rounded.Info, "App Information")
            },
        )
        NavigationDrawerItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "Bug Report")
            },
            icon = {
                Icon(Icons.Rounded.BugReport, "Bug Report")
            },
        )
        NavigationDrawerItem(
            selected = false,
            onClick = { /*TODO*/ },
            label = {
                Text(text = "Website")
            },
            icon = {
                Icon(Icons.Rounded.Web, "Website")
            },
        )
    }
}

@Preview
@Composable
@ExperimentalMaterial3Api
fun MainScreenCompactPreview() {
    MainScreen(windowSize = WindowWidthSizeClass.Compact)
}

@Composable
@ExperimentalMaterial3Api
fun MainScreen(
    windowSize: WindowWidthSizeClass
) {
    val navigationDrawerPermanent = windowSize == WindowWidthSizeClass.Expanded
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (navigationDrawerPermanent)
        PermanentNavigationDrawer(
            drawerContent = { MainScreenDrawerContent() },
        ) {
            MainScreenContent {}
        }
    else
        ModalNavigationDrawer(
            drawerContent = { MainScreenDrawerContent() },
            drawerState = drawerState,
        ) {
            MainScreenContent { scope.launch { drawerState.open() } }
        }
}
