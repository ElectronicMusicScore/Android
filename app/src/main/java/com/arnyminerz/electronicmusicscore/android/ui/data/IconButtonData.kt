package com.arnyminerz.electronicmusicscore.android.ui.data

import androidx.compose.ui.graphics.vector.ImageVector

data class IconButtonData(
    val icon: ImageVector,
    val description: String,
    val callback: () -> Unit,
)
