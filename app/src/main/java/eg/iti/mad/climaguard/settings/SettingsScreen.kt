package eg.iti.mad.climaguard.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val isGpsEnabled by viewModel.gpsEnabled.collectAsState()
    val selectedTemperatureUnit by viewModel.tempUnit.collectAsState()
    val selectedWindSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val selectedLanguage by viewModel.language.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))

        // Location Selection
        Text(text = "Location", style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RadioButton(selected = isGpsEnabled, onClick = { viewModel.saveSettings(true, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage) })
            Text(text = "Use GPS", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(selected = !isGpsEnabled, onClick = { viewModel.saveSettings(false, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage) })
            Text(text = "Pick from Map", modifier = Modifier.padding(start = 8.dp))
        }

        // Temperature Unit Selection
        Text(text = "Temperature Unit", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        TemperatureUnitDropdown(selectedTemperatureUnit) { newUnit ->
            viewModel.saveSettings(isGpsEnabled, newUnit, if (newUnit == "imperial") "miles/hour" else "meter/sec", selectedLanguage)
        }

        // Wind Speed Unit Selection
        Text(text = "Wind Speed Unit", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        WindSpeedUnitDropdown(selectedWindSpeedUnit) { newUnit ->
            viewModel.saveSettings(isGpsEnabled, if (newUnit == "miles/hour") "imperial" else "metric", newUnit, selectedLanguage)
        }

        // Language Selection
        Text(text = "Language", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        LanguageDropdown(selectedLanguage) { viewModel.saveSettings(isGpsEnabled, selectedTemperatureUnit, selectedWindSpeedUnit, it) }

        // Save Button
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.saveSettings(isGpsEnabled, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save Settings")
        }
    }
}



@Composable
fun TemperatureUnitDropdown(selectedUnit: String, onUnitSelected: (String) -> Unit) {
    val units = listOf("Kelvin", "Celsius", "Fahrenheit")
    val apiUnits = mapOf("Kelvin" to "standard", "Celsius" to "metric", "Fahrenheit" to "imperial")

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().clickable { expanded = true }) {
        Text(text = units.find { apiUnits[it] == selectedUnit } ?: "Celsius", modifier = Modifier.padding(12.dp))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            units.forEach { unit ->
                DropdownMenuItem(text = { Text(unit) }, onClick = {
                    onUnitSelected(apiUnits[unit] ?: "metric")
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun WindSpeedUnitDropdown(selectedUnit: String, onUnitSelected: (String) -> Unit) {
    val windUnits = listOf("meter/sec", "miles/hour")

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().clickable { expanded = true }) {
        Text(text = selectedUnit, modifier = Modifier.padding(12.dp))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            windUnits.forEach { unit ->
                DropdownMenuItem(text = { Text(unit) }, onClick = {
                    onUnitSelected(unit)
                    expanded = false
                })
            }
        }
    }
}



@Composable
fun LanguageDropdown(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf("English" to "en", "Arabic" to "ar")
    var expanded by remember { mutableStateOf(false) }

    // Find display name from the stored value
    val displayName = languages.find { it.second == selectedLanguage }?.first ?: "English"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }) {
        Text(text = displayName, modifier = Modifier.padding(12.dp))
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            languages.forEach { (name, value) ->
                DropdownMenuItem(text = { Text(name) }, onClick = {
                    onLanguageSelected(value) // Save the selected language as "en" or "ar"
                    expanded = false
                })
            }
        }
    }
}


