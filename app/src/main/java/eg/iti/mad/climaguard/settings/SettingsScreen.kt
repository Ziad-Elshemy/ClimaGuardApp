package eg.iti.mad.climaguard.settings

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    val isGpsEnabled by viewModel.gpsEnabled.collectAsState()
    val selectedTemperatureUnit by viewModel.tempUnit.collectAsState()
    val selectedWindSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val selectedLanguage by viewModel.language.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF95CBD2))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 📍 Location Selection
            SectionTitle("Location", Icons.Default.LocationOn)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = isGpsEnabled,
                    onClick = { viewModel.saveSettings(true, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage) }
                )
                Text(text = "Use GPS", modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(
                    selected = !isGpsEnabled,
                    onClick = { viewModel.saveSettings(false, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage) }
                )
                Text(text = "Pick from Map", modifier = Modifier.padding(start = 8.dp))
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 🌡 Temperature Unit Selection
            SectionTitle("Temperature Unit", Icons.Default.Thermostat)
            TemperatureUnitDropdown(selectedTemperatureUnit) { newUnit ->
                viewModel.saveSettings(isGpsEnabled, newUnit, if (newUnit == "imperial") "miles/hour" else "meter/sec", selectedLanguage)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 💨 Wind Speed Unit Selection
            SectionTitle("Wind Speed Unit", Icons.Default.Speed)
            WindSpeedUnitDropdown(selectedWindSpeedUnit) { newUnit ->
                viewModel.saveSettings(isGpsEnabled, if (newUnit == "miles/hour") "imperial" else "metric", newUnit, selectedLanguage)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // 🌍 Language Selection
            SectionTitle("Language", Icons.Default.Language)
            LanguageDropdown(selectedLanguage) {
                viewModel.saveSettings(isGpsEnabled, selectedTemperatureUnit, selectedWindSpeedUnit, it)
            }

            // 💾 Save Button
            Spacer(modifier = Modifier.height(24.dp))
            val context = LocalContext.current
            Button(
                onClick = {
                    viewModel.saveSettings(isGpsEnabled, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage)
                    // Restart the MainActivity to apply changes
                    val activity = context as? Activity
                    val intent = activity?.intent
                    activity?.finish()
                    activity?.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = "Save Settings", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
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


