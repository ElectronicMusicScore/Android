package com.arnyminerz.electronicmusicscore.android.ui.utils

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import dev.chrisbanes.snapper.ExperimentalSnapperApi

/**
 * A horizontally scrolling layout that allows users to flip between items to the left and right.
 *
 * @sample com.google.accompanist.sample.pager.HorizontalPagerSample
 *
 * @param list the data of the pages to display.
 * @param modifier the modifier to apply to this layout.
 * @param state the state object to be used to control or observe the pager's state.
 * @param reverseLayout reverse the direction of scrolling and layout, when `true` items will be
 * composed from the end to the start and [PagerState.currentPage] == 0 will mean
 * the first item is located at the end.
 * @param itemSpacing horizontal spacing to add between items.
 * @param flingBehavior logic describing fling behavior.
 * @param key the scroll position will be maintained based on the key, which means if you
 * add/remove items before the current visible item the item with the given key will be kept as the
 * first visible one.
 * @param userScrollEnabled whether the scrolling via the user gestures or accessibility actions
 * is allowed. You can still scroll programmatically using the state even when it is disabled.
 * @param content a block which describes the content. Inside this block you can reference
 * [PagerScope.currentPage] and other properties in [PagerScope].
 */
@Composable
@ExperimentalPagerApi
@OptIn(ExperimentalSnapperApi::class)
fun <A> HorizontalPager(
    list: List<A>,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(),
    reverseLayout: Boolean = false,
    itemSpacing: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: FlingBehavior = PagerDefaults.flingBehavior(
        state = state,
        endContentPadding = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
    ),
    key: ((page: Int) -> Any)? = null,
    userScrollEnabled: Boolean = true,
    content: @Composable (PagerScope.(A) -> Unit)
) {
    com.google.accompanist.pager.HorizontalPager(
        count = list.size,
        modifier,
        state,
        reverseLayout,
        itemSpacing,
        contentPadding,
        verticalAlignment,
        flingBehavior,
        key,
        userScrollEnabled
    ) { index -> content(list[index]) }
}
