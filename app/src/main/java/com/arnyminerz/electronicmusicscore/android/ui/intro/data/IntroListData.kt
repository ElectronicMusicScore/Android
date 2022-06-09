package com.arnyminerz.electronicmusicscore.android.ui.intro.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

data class IntroListData<A>(
    val appearance: Appearance,
    val loading: MutableState<Boolean> = mutableStateOf(false),
    val context: IntroListDataContext = object : IntroListDataContext() { },
    val items: MutableState<List<A>> = mutableStateOf(emptyList()),
    val itemPrototype: @Composable IntroListDataContext.(item: A) -> Unit,
) {
    data class Appearance(
        val startLoadingText: String,
        val stopLoadingText: String,
        val loadingIcon: ImageVector,
        val idleIcon: ImageVector,
    )
}

abstract class IntroListDataContext {
    var actionCallback: (responseCode: Int, extras: Map<String, Any>) -> Unit = { _, _ -> }
}
