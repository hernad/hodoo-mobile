package ba.out.bring.hodooMobile

import com.russhwolf.settings.Settings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual fun createSettings(context: Any?): Settings {
    val preferences = Preferences.userRoot().node("hodoo-mobile")
    return PreferencesSettings(preferences)
}