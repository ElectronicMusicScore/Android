package com.arnyminerz.electronicmusicscore.android.ui.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.arnyminerz.electronicmusicscore.android.R
import com.arnyminerz.electronicmusicscore.android.ui.intro.data.IntroPageData
import com.arnyminerz.electronicmusicscore.android.ui.utils.HorizontalPager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

const val CALLBACK_CODE_START_LOADING = 654
const val CALLBACK_CODE_STOP_LOADING = 884

@ExperimentalMaterial3Api
private class IntroWindowPagesProvider : PreviewParameterProvider<List<IntroPageData<*>>> {
    override val values: Sequence<List<IntroPageData<*>>> = sequenceOf(
        listOf(
            IntroPages.PAGE_1,
            IntroPages.PAGE_2,
            IntroPages.PAGE_3,
        )
    )
}

@Composable
@Preview
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
fun IntroWindow(
    @PreviewParameter(IntroWindowPagesProvider::class) pages: List<IntroPageData<*>>,
    initialPage: Int = 0,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage)
    val inspectionMode = LocalInspectionMode.current

    var pageData by remember { mutableStateOf(pages[initialPage]) }
    val permissionState = pageData.permissionsRequest?.let { rememberMultiplePermissionsState(it) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { pageIndex ->
            pageData = pages[pageIndex]
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (!inspectionMode && permissionState != null && !permissionState.allPermissionsGranted)
                        permissionState.launchMultiplePermissionRequest()
                    else
                        pageData.listItems
                            ?.let {
                                it.context.actionCallback(
                                    if (it.loading.value)
                                        CALLBACK_CODE_STOP_LOADING
                                    else
                                        CALLBACK_CODE_START_LOADING,
                                    emptyMap(),
                                )
                            }
                            ?: scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (!inspectionMode && permissionState != null && !permissionState.allPermissionsGranted)
                            Icons.Rounded.Security
                        else
                            pageData.listItems?.let {
                                if (it.loading.value)
                                    it.appearance.loadingIcon
                                else
                                    it.appearance.idleIcon
                            } ?: Icons.Rounded.ChevronRight,
                        contentDescription = stringResource(
                            if (!inspectionMode && permissionState != null && !permissionState.allPermissionsGranted)
                                R.string.fab_desc_authorise
                            else
                                R.string.fab_desc_continue
                        ),
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Text(
                        if (!inspectionMode && permissionState != null && !permissionState.allPermissionsGranted)
                            stringResource(R.string.action_authorise)
                        else
                            pageData.listItems
                                ?.let {
                                    if (it.loading.value)
                                        it.appearance.stopLoadingText
                                    else
                                        it.appearance.startLoadingText
                                } ?: stringResource(R.string.action_next),
                    )
                }
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AnimatedVisibility(
                    visible = pagerState.currentPage > 0,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp),
                ) {
                    TextButton(
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.action_go_back),
                        )
                    }
                }

                HorizontalPager(
                    pages,
                    state = pagerState,
                    userScrollEnabled = permissionState?.allPermissionsGranted != false,
                ) { page ->
                    IntroPage(data = page)
                }
            }
        }
    )
}
