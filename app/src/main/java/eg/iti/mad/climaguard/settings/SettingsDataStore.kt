package eg.iti.mad.climaguard.settings

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// create a dataStore with specific name
private val Context.dataStore by preferencesDataStore(name = "settings_prefs")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    // saved key-value pairs
    companion object {
        private val GPS_ENABLED_KEY = booleanPreferencesKey("gps_enabled")
        private val TEMP_UNIT_KEY = stringPreferencesKey("temp_unit")
        private val WIND_SPEED_UNIT_KEY = stringPreferencesKey("wind_speed_unit")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
    }

    // read data as Flow
    val gpsEnabled: Flow<Boolean> = dataStore.data.map { it[GPS_ENABLED_KEY] ?: true }
    val tempUnit: Flow<String> = dataStore.data.map { it[TEMP_UNIT_KEY] ?: "metric" }
    val windSpeedUnit: Flow<String> = dataStore.data.map { it[WIND_SPEED_UNIT_KEY] ?: "metric" }
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }

    // save fun for configurations
    suspend fun saveSettings(isGpsEnabled: Boolean, tempUnit: String, windSpeedUnit: String, language: String) {
        dataStore.edit { settings ->
            settings[GPS_ENABLED_KEY] = isGpsEnabled
            settings[TEMP_UNIT_KEY] = tempUnit
            settings[WIND_SPEED_UNIT_KEY] = windSpeedUnit
            settings[LANGUAGE_KEY] = language
        }
    }
}
