package eg.iti.mad.climaguard.alarm


import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.model.AlarmEntity

import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.navigation.NavigationRoute
import eg.iti.mad.climaguard.worker.AlarmWorker
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(navController: NavController, viewModel: AlarmViewModel) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var selectedLocation by remember { mutableStateOf<LocationEntity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // receive data from map
    val resultJson = navController.currentBackStackEntry?.savedStateHandle?.get<String>("selected_location")
    Log.d("AlarmScreen", "resultJson: $resultJson")
    val result = resultJson?.let {
        Gson().fromJson(it, LocationEntity::class.java)
    }
    LaunchedEffect(result) {
        Log.d("AlarmScreen", "result: $result")
        result?.let {
            selectedLocation = it
            showBottomSheet = true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(NavigationRoute.Maps.createRoute("alarm")) // show map screen
            }) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Open Map")
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            selectedLocation?.let {
                Text("Selected Location: ${it.name}") //show address after back form map
            }
        }
    }

    // show Bottom Sheet when user select location
    if (showBottomSheet) {
        Log.d("AlarmScreen", "AlarmScreen: showBottomSheet")
        AlarmSettingsBottomSheet(
            location = selectedLocation!!,
            onDismiss = { showBottomSheet = false },
            onSave = { alarmEntity ->
                viewModel.saveAlarm(alarmEntity)
                showBottomSheet = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmSettingsBottomSheet(
    location: LocationEntity,
    onDismiss: () -> Unit,
    onSave: (AlarmEntity) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var notificationType by remember { mutableStateOf("Notification") }

    val context = LocalContext.current


    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Set Alarm for ${location.name}", fontWeight = FontWeight.Bold)

            // date picker button
            Button(onClick = { showDatePicker = true }) {
                Text(stringResource(R.string.select_date, selectedDate))
            }
            if (showDatePicker) {
                DatePickerDialog(
                    initialDate = selectedDate,
                    onDateSelected = { newDate ->
                        selectedDate = newDate
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            // time picker button
            Button(onClick = { showTimePicker = true }) {
                Text(stringResource(R.string.select_time, selectedTime))
            }
            if (showTimePicker) {
                TimePickerDialog(
                    initialTime = selectedTime,
                    onTimeSelected = { newTime ->
                        selectedTime = newTime
                        showTimePicker = false
                    },
                    onDismiss = { showTimePicker = false }
                )
            }

            // select type (Notification or Alarm)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = notificationType == "Notification",
                    onClick = { notificationType = "Notification" }
                )
                Text(stringResource(R.string.notification), modifier = Modifier.padding(start = 4.dp))

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = notificationType == "Alarm",
                    onClick = { notificationType = "Alarm" }
                )
                Text(stringResource(R.string.alarm_type), modifier = Modifier.padding(start = 4.dp))
            }

            // save button
            Button(onClick = {
                val dateTime = selectedDate.atTime(selectedTime)
                val dateTimeInMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val currentTime = System.currentTimeMillis()
                val delay = dateTimeInMillis - currentTime

                Log.d("DEBUG", "Alarm scheduled after $delay ms")

                if (delay > 0) {
                    scheduleAlarmWorker(context,dateTimeInMillis, location.name, "Your alarm is set!", notificationType, delay,location.lat,location.lon)
                }

                val alarmEntity = AlarmEntity(
                    locationName = location.name,
                    latitude = location.lat,
                    longitude = location.lon,
                    dateTime = dateTimeInMillis,
                    type = notificationType
                )
                onSave(alarmEntity)
            }) {
                Text(stringResource(R.string.save))
            }

            // cancel button
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    }
}

fun scheduleAlarmWorker(
    context: Context,
    dateTime: Long,
    title: String,
    message: String,
    type: String,
    delay: Long,
    lat: Double,
    lon: Double
) {
    val data = workDataOf(
        "DATE_TIME" to dateTime,
        "TITLE" to title,
        "MESSAGE" to message,
        "TYPE" to type,
        "LAT" to lat,
        "LON" to lon
    )

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val alarmRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,5,TimeUnit.SECONDS
        )
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(alarmRequest)
}






