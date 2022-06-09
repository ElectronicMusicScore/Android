package com.arnyminerz.electronicmusicscore.android.ui.utils

/**
 * Adds the item [item] to the list, and returns [this].
 * @author Arnau Mora
 * @since 20220609
 * @param item The item to add.
 * @return [this].
 */
fun <A> MutableCollection<A>.append(item: A): MutableCollection<A> {
    add(item)
    return this
}
