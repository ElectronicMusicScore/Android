package com.arnyminerz.electronicmusicscore.android.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arnyminerz.electronicmusicscore.android.ui.data.IconButtonData

@Composable
@Preview(
    showBackground = true,
)
fun CardPanelRow(
    modifier: Modifier = Modifier,
    title: String = "Title",
    message: String = "Message content",
    action: IconButtonData? = null,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .height(50.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
            )
        }
        if (action != null)
            IconButton(onClick = action.callback) {
                Icon(action.icon, action.description)
            }
    }
}