package com.arnyminerz.electronicmusicscore.android.ui.preview.providers

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class WindowWidthSizeClassProvider : PreviewParameterProvider<WindowWidthSizeClass> {
    override val values: Sequence<WindowWidthSizeClass> =
        sequenceOf(
            WindowWidthSizeClass.Compact,
            WindowWidthSizeClass.Medium,
            WindowWidthSizeClass.Expanded,
        )
}