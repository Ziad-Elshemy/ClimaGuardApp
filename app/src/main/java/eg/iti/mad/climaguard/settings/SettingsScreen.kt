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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eg.iti.mad.climaguard.R

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
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF8E9E4), Color(0xFFC3D1F3)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, 4000f)
                    )
                )
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Location Selection
            SectionTitle(stringResource(R.string.location), Icons.Default.LocationOn)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(
                    selected = isGpsEnabled,
                    onClick = { viewModel.saveSettings(true, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage) }
                )
                Text(text = stringResource(R.string.use_gps), modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(
                    selected = !isGpsEnabled,
                    onClick = { viewModel.saveSettings(false, selectedTemperatureUnit, selectedWindSpeedUnit, selectedLanguage) }
                )
                Text(text = stringResource(R.string.pick_from_map), modifier = Modifier.padding(start = 8.dp))
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Temperature Unit Selection
            SectionTitle(stringResource(R.string.temperature_unit), Icons.Default.Thermostat)
            TemperatureUnitDropdown(selectedTemperatureUnit) { newUnit ->
                viewModel.saveSettings(isGpsEnabled, newUnit, if (newUnit == "imperial") "miles/hour" else "meter/sec", selectedLanguage)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Wind Speed Unit Selection
            SectionTitle(stringResource(R.string.wind_speed_unit), Icons.Default.Speed)
            WindSpeedUnitDropdown(selectedWindSpeedUnit) { newUnit ->
                viewModel.saveSettings(isGpsEnabled, if (newUnit == "miles/hour") "imperial" else "metric", newUnit, selectedLanguage)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Language Selection
            SectionTitle(stringResource(R.string.language), Icons.Default.Language)
            LanguageDropdown(selectedLanguage) {
                viewModel.saveSettings(isGpsEnabled, selectedTemperatureUnit, selectedWindSpeedUnit, it)
            }

            // Save Button
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Text(text = stringResource(R.string.save_settings), fontSize = 18.sp)
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
    val units = listOf(stringResource(R.string.kelvin),
        stringResource(R.string.celsius), stringResource(R.string.fahrenheit)
    )
    val apiUnits = mapOf(stringResource(R.string.kelvin) to "standard", stringResource(R.string.celsius) to "metric", stringResource(R.string.fahrenheit) to "imperial")

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }) {
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
    val meterPerSec = stringResource(R.string.meter_sec)
    val milesPerHour = stringResource(R.string.miles_hour)

    val displayToApiMap = mapOf(
        meterPerSec to "meter/sec",
        milesPerHour to "miles/hour"
    )

    val apiToDisplayMap = mapOf(
        "meter/sec" to meterPerSec,
        "miles/hour" to milesPerHour
    )

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = true }) {
        Text(
            text = apiToDisplayMap[selectedUnit] ?: meterPerSec,
            modifier = Modifier.padding(12.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            displayToApiMap.forEach { (display, apiValue) ->
                DropdownMenuItem(
                    text = { Text(display) },
                    onClick = {
                        onUnitSelected(apiValue)
                        expanded = false
                    }
                )
            }
        }
    }
}




@Composable
fun LanguageDropdown(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(stringResource(R.string.english) to "en", stringResource(R.string.arabic) to "ar")
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


