package eg.iti.mad.climaguard.map

import android.location.Location
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapScreen(viewModel: MapViewModel, currentLocation: Location) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchSuggestions by viewModel.searchSuggestions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var selectedLocation by remember { mutableStateOf(LatLng(currentLocation.latitude, currentLocation.longitude)) }

    val initialLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 10f)
    }

    val markerState = rememberMarkerState(position = selectedLocation)

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchLocation(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Search for a place...") }
        )

        // Suggestions List
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(searchSuggestions) { suggestion ->
                Text(
                    text = suggestion.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchQuery = suggestion.name
                            selectedLocation = LatLng(suggestion.lat, suggestion.lon)
                            markerState.position = selectedLocation

                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(selectedLocation, 12f),
                                    durationMs = 1000
                                )
                            }
                        }
                        .padding(8.dp)
                )
            }
        }

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
    }

    LaunchedEffect(selectedLocation) {
        markerState.position = selectedLocation
    }

    LaunchedEffect(searchResults) {
        searchResults.firstOrNull()?.let { newLocation ->
            selectedLocation = newLocation
            markerState.position = newLocation
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(newLocation, 12f),
                durationMs = 1000
            )
        }
    }
}
