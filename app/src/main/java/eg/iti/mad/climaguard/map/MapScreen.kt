package eg.iti.mad.climaguard.map

import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.*
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.utils.LocationPreferences
import kotlinx.coroutines.launch

@Composable
fun MapScreen(viewModel: MapViewModel,
              currentLocation: Location,
              screenType: String,  //receive screen type
              navController: NavController
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var selectedLocation by remember {
        mutableStateOf(
            LatLng(
                currentLocation.latitude,
                currentLocation.longitude
            )
        )
    }

    val initialLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 10f)
    }

    val markerState = rememberMarkerState(position = selectedLocation)

    val coroutineScope = rememberCoroutineScope()

    var isSearchVisible by remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { contentPadding ->
        Box(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()) {
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    markerState.position = latLng
                    Log.d("MapScreen", "User selected location: $latLng")

                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(latLng, 12f),
                            durationMs = 1000
                        )
                    }
                }
            ) {
                Marker(
                    state = markerState,
                    title = "Selected Location"
                )
            }

            val context = LocalContext.current

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchLocation(it)
                    isSearchVisible = it.isNotEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_for_a_place)) }
            )

            // Search Results List
            if (isSearchVisible) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                ) {
                    items(searchSuggestions) { suggestion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .clickable {
                                    searchQuery = suggestion.name
                                    selectedLocation = LatLng(suggestion.lat, suggestion.lon)
                                    markerState.position = selectedLocation
                                    isSearchVisible = false

                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(
                                                selectedLocation,
                                                12f
                                            ),
                                            durationMs = 1000
                                        )
                                    }
                                },
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Location",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "${suggestion.name}, ${suggestion.country}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            val locationPrefs = remember { LocationPreferences(context) }

            FloatingActionButton(
                onClick = {
                    viewModel.getCityAndCountryFromLocation(
                        selectedLocation.latitude,
                        selectedLocation.longitude,
                        context
                    ) { city, country ->
                        if (screenType == "favorite") {
                            // save item to fav
                            viewModel.addLocationToFav(
                                LocationEntity(
                                    name = city,
                                    country = country,
                                    lat = selectedLocation.latitude,
                                    lon = selectedLocation.longitude
                                )
                            )
                        }
                        else if (screenType == "alarm") {
                            val locationEntity = LocationEntity(
                                name = city,
                                country = country,
                                lat = selectedLocation.latitude,
                                lon = selectedLocation.longitude
                            )
                            val locationJson = Gson().toJson(locationEntity)
                            Log.d("MapScreen", "locationJson: $locationJson")
                            // back to alarm screen with selected location
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_location", locationJson)
                        }
                        else if (screenType == "settings") {
                            locationPrefs.saveLocation(
                                selectedLocation.latitude,
                                selectedLocation.longitude
                            )
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text =
                    if (screenType == "favorite") stringResource(R.string.add_location)
                    else if (screenType == "alarm")  stringResource(R.string.select_location)
                    else stringResource(R.string.save_location),
                    fontSize = 16.sp
                )
            }
        }


        // Show message when location is added
        LaunchedEffect(Unit) {
            viewModel.message.collect { message ->
                if (!message.isNullOrBlank()) {
                    snackBarHostState.showSnackbar(
                        message = message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

        }

    }


}

