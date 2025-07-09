package org.example.project

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

actual fun createSettings(context: Any?): Settings {
    val androidContext = context as Context
    return SharedPreferencesSettings(androidContext.getSharedPreferences("odoo_settings", Context.MODE_PRIVATE))
}