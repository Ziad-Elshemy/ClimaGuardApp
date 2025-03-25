package eg.iti.mad.climaguard.map

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(viewModel: MapViewModel, currentLocation: Location) {
    val searchResults by viewModel.searchResults.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var selectedLocation by remember { mutableStateOf(LatLng(currentLocation.latitude, currentLocation.longitude)) }

    val initialLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 10f)
    }

    // Store the latest camera position
    var cameraMarkerPosition by remember { mutableStateOf(initialLocation) }

    // Move markerState outside of GoogleMap and remember it
    val markerState = rememberMarkerState(position = cameraMarkerPosition)

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar (unchanged)
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

        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
                cameraMarkerPosition = latLng
                Log.d("MapScreen", "selectedLocation: $selectedLocation")
            }
        ) {
            // Marker using the stable markerState
            Marker(
                state = markerState,
                title = "Selected Location"
            )

            // Markers for search results
            searchResults.forEach { latLng ->
                Marker(
                    state = rememberMarkerState(position = latLng),
                    title = "Search Result",
                    snippet = "Tap to navigate"
                )
            }
        }
    }

    // Update marker position when cameraMarkerPosition changes
    LaunchedEffect(cameraMarkerPosition) {
        markerState.position = cameraMarkerPosition
    }

    // Run camera animation when selectedLocation changes
    LaunchedEffect(selectedLocation) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(selectedLocation, 12f),
            durationMs = 1000
        )
    }

    // Move the camera when a search result is selected
    LaunchedEffect(searchResults) {
        searchResults.firstOrNull()?.let { newLocation ->
            selectedLocation = newLocation
            cameraMarkerPosition = newLocation
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(newLocation, 12f),
                durationMs = 1000
            )
        }
    }
}


