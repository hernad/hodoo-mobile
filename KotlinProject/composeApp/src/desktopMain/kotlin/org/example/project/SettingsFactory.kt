package org.example.project

import com.russhwolf.settings.Settings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual fun createSettings(): Settings {
    val preferences = Preferences.userRoot().node("hodoo-mobile")
    return PreferencesSettings(preferences)
}