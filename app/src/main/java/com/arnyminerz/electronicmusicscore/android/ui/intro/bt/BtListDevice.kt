package com.arnyminerz.electronicmusicscore.android.ui.intro.bt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.arnyminerz.electronicmusicscore.android.R

@Preview(showBackground = true)
@Composable
@ExperimentalMaterial3Api
fun BtListDevice(
    @PreviewParameter(BtListDeviceDataPPP::class) data: BtListDeviceData,
    connectCallback: (() -> Unit)? = null
) {
    var showingDialog by remember { mutableStateOf(false) }

    if (showingDialog)
        AlertDialog(
            onDismissRequest = { showingDialog = false },
            confirmButton = {
                Button(onClick = { connectCallback?.invoke() }) {
                    Text(
                        text = stringResource(R.string.dialog_connect_device_confirm)
                    )
                }
            },
            dismissButton = {
                Button(onClick = { showingDialog = false }) {
                    Text(
                        stringResource(R.string.action_cancel)
                    )
                }
            },
            title = {
                Text(
                    stringResource(R.string.dialog_connect_device_title, data.name)
                )
            },
            text = {
                Text(
                    stringResource(R.string.dialog_connect_device_body)
                )
            }
        )

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
            ) {
                Text(
                    text = data.type,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            IconButton(
                onClick = { showingDialog = true },
            ) {
                Icon(
                    Icons.Rounded.Link,
                    stringResource(R.string.button_desc_connect)
                )
            }
        }
    }
}
