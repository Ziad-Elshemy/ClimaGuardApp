package eg.iti.mad.climaguard.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsDataStore: SettingsDataStore) : ViewModel() {

    val gpsEnabled = settingsDataStore.gpsEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val tempUnit = settingsDataStore.tempUnit.stateIn(viewModelScope, SharingStarted.Lazily, "Celsius")
    val windSpeedUnit = settingsDataStore.windSpeedUnit.stateIn(viewModelScope, SharingStarted.Lazily, "meter/sec")
    val language = settingsDataStore.language.stateIn(viewModelScope, SharingStarted.Lazily, "English")

    fun saveSettings(isGpsEnabled: Boolean, tempUnit: String, windSpeedUnit: String, language: String) {
        viewModelScope.launch {
            settingsDataStore.saveSettings(isGpsEnabled, tempUnit, windSpeedUnit, language)
        }
    }
}

class SettingsFactory(private val settingsDataStore: SettingsDataStore):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(settingsDataStore) as T
    }

}