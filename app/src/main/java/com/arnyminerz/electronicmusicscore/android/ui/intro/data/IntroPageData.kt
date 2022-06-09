package com.arnyminerz.electronicmusicscore.android.ui.intro.data

data class IntroPageData <L>(
    val title: String,
    val content: String,
    val headerEmoji: String,
    val permissionsRequest: List<String>? = null,
    val listItems: IntroListData<L>? = null,
)
