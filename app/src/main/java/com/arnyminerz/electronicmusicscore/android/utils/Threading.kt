package com.arnyminerz.electronicmusicscore.android.utils

import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Runs the code inside [block] asynchronously, in the IO thread.
 * @author Arnau Mora
 * @since 20220906
 * @param block The block of code to run.
 */
fun doAsync(@WorkerThread block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch { block(this) }
}

/**
 * Runs the contents of [block] in the UI thread.
 * @author Arnau Mora
 * @since 20220609
 * @param block The block of code to run.
 * @param R The return type of the block of code.
 * @return The result of the content returned by [block].
 */
suspend fun <R> uiContext(@UiThread block: suspend CoroutineScope.() -> R) =
    withContext(Dispatchers.Main) { block(this) }
