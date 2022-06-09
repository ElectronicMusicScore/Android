package com.arnyminerz.electronicmusicscore.android.ui.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arnyminerz.electronicmusicscore.android.ui.intro.data.IntroPageData

@ExperimentalMaterial3Api
class IntroPagePreviewProvider : PreviewParameterProvider<IntroPageData<*>> {
    override val values: Sequence<IntroPageData<*>> = sequenceOf(
        IntroPages.PAGE_1,
        IntroPages.PAGE_2,
        IntroPages.PAGE_3,
    )
}

@Composable
@Preview(
    showBackground = true,
)
@ExperimentalMaterial3Api
fun <A> IntroPage(
    @PreviewParameter(provider = IntroPagePreviewProvider::class)
    data: IntroPageData<A>,
) {
    val topPadding by animateDpAsState(
        targetValue = if (data.listItems == null || (!data.listItems.loading.value && data.listItems.items.value.isEmpty()))
            150.dp
        else
            16.dp,
    )

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = data.headerEmoji,
            fontSize = 65.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = topPadding),
        )
        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(start = 24.dp, end = 16.dp, top = topPadding)
        )
        Text(
            text = data.content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 24.dp, end = 16.dp, top = 4.dp)
        )

        data.listItems?.let { listItems ->
            val items by listItems.items

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
            ) {
                items(items) { item ->
                    listItems.itemPrototype(listItems.context, item)
                }
            }
        }

        AnimatedVisibility(
            visible = data.listItems?.loading?.value == true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
