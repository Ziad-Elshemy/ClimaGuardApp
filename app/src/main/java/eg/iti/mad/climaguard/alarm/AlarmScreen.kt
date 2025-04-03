package eg.iti.mad.climaguard.alarm


import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.favorite.FavoriteItem
import eg.iti.mad.climaguard.home.LoadingIndicator
import eg.iti.mad.climaguard.model.AlarmEntity

import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.navigation.NavigationRoute
import eg.iti.mad.climaguard.worker.AlarmWorker
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import java.util.concurrent.TimeUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(navController: NavController, viewModel: AlarmViewModel) {

    viewModel.getAlarms()

    val uiState by viewModel.alarmsList.collectAsStateWithLifecycle()

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
            if (selectedLocation != it) {
                selectedLocation = it
                showBottomSheet = true
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selected_location")
            }
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            when(uiState){
                is Response.Loading ->{
                    LoadingIndicator()
                }

                is Response.Success -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items((uiState as Response.Success).data){ alarm ->
                            AlarmItem(alarm = alarm,navController,
                                onDeleteFromAlarmClicked = {
                                    try {
                                        val uuid = UUID.fromString(alarm.uuid)
                                        viewModel.deleteAlarm(alarm)
                                        cancelAlarmWorker(context,uuid)
                                    }catch (ex:Exception){
                                        Log.e("onDeleteFromAlarmClicked","Invalid UUID")
                                        viewModel.deleteAlarmById(alarm.dateTime)
                                    }

                                })
                        }

                    }
                }
                is Response.Failure -> {
                    Text(
                        text = "sorry there is an error",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        fontSize = 22.sp
                    )
                }
            }

            LaunchedEffect(Unit) {
                viewModel.message.collect{message->
                    if (!message.isNullOrBlank()){
                        snackBarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    // show Bottom Sheet when user select location
    if (showBottomSheet) {
        Log.d("AlarmScreen", "AlarmScreen: showBottomSheet")
        AlarmSettingsBottomSheet(
            viewModel = viewModel,
            location = selectedLocation!!,
            onDismiss = { showBottomSheet = false },
            onSave = { alarmEntity ->
                viewModel.saveAlarm(alarmEntity)
                showBottomSheet = false
            }
        )
    }
}

@Composable
fun AlarmItem(alarm: AlarmEntity,navController: NavController,
                 onDeleteFromAlarmClicked: ()->Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFDFFCE5) // Light blue background
        ),
        modifier = Modifier
            .fillMaxWidth()
//            .clickable { navController.navigate(NavigationRoute.FavItem.createRoute(place.lat, place.lon)) }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Icon (Placeholder for Image)
            Image(
                painter = painterResource(R.drawable.ic_favourite_location),
                contentDescription = alarm.type,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f) // Takes remaining space
                    .padding(start = 10.dp)
            ) {
                // Location Name
                Text(
                    text = alarm.locationName,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Location Name
                Text(
                    text = alarm.type,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

            }

            // Delete Icon Button
            IconButton(
                onClick = onDeleteFromAlarmClicked,
                modifier = Modifier
                    .size(40.dp) // Adjust size if needed
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "Delete",
                    tint = Color(0xFFE91E63) // Pinkish color like the button before
                )
            }
        }
    }
}

fun cancelAlarmWorker(context: Context, workId: UUID) {
    WorkManager.getInstance(context).cancelWorkById(workId)
    Log.d("cancelAlarmWorker", " $workId canceled!")
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmSettingsBottomSheet(
    viewModel: AlarmViewModel,
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

                Log.i("DEBUG", "Alarm scheduled after $delay ms")

                var workId = ""
                if (delay > 0) {
                    val uuid = viewModel.scheduleAlarmWorker(context,dateTimeInMillis, location.name, "Your alarm is set!", notificationType, delay,location.lat,location.lon)
                    workId = uuid.toString()
                }

                val alarmEntity = AlarmEntity(
                    uuid = workId,
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

