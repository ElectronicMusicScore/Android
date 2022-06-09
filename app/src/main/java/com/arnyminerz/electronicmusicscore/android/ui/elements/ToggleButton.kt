package com.arnyminerz.electronicmusicscore.android.ui.elements

import android.widget.ToggleButton
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

private class ToggleButtonPreviewCheckedProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}

@Preview(
    showBackground = true,
)
@Composable
@ExperimentalMaterial3Api
private fun ToggleButtonPreview(
    @PreviewParameter(ToggleButtonPreviewCheckedProvider::class) checked: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ToggleButton(
            checked = checked,
            onCheckedChange = {},
            icon = Icons.Rounded.Add,
            text = "Demo text"
        )
    }
}

@Composable
@ExperimentalMaterial3Api
fun RowScope.ToggleButton(
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit,
    icon: ImageVector,
    text: String,
) {
    FilledTonalIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
        colors = IconButtonDefaults.filledTonalIconToggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = MaterialTheme.colorScheme.primaryContainer
                .copy(ContentAlpha.disabled),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                // .copy(ContentAlpha.medium),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = text)
            Text(
                text,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
