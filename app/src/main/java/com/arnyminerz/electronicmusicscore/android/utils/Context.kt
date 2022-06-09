package com.arnyminerz.electronicmusicscore.android.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "settings"

/**
 * Returns the [DataStore] instance for storing preferences.
 * @author Arnau Mora
 * @since 20220609
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

/**
 * Checks if a permission is granted.
 * @author Arnau Mora
 * @since 20220609
 * @param permission The permission to check for.
 * @return True if the permission is granted, false otherwise.
 */
fun Context.permissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
