package com.arnyminerz.electronicmusicscore.android.utils

/**
 * Runs the code block only if the value of the object is true.
 * @author Arnau Mora
 * @since 20220609
 * @param block The block of code to run.
 * @return true if the value of the object is true, null otherwise.
 */
fun Boolean.then(block: () -> Unit): Boolean? {
    return if (this) {
        block()
        true
    } else null
}

/**
 * Runs the code block only if the value of the object is false.
 * @author Arnau Mora
 * @since 20220609
 * @param block The block of code to run.
 * @return false if the value of the object is false, null otherwise.
 */
fun Boolean.ifFalse(block: () -> Unit): Boolean? {
    return if (this) {
        block()
        false
    } else null
}
