package eg.iti.mad.climaguard.map

import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class MapViewModel(private val geocoder: Geocoder) : ViewModel() {
    private val _searchQuery = MutableSharedFlow<String>()
    val searchQuery = _searchQuery.asSharedFlow()

    private val _searchResults = MutableStateFlow<List<LatLng>>(emptyList())
    val searchResults: StateFlow<List<LatLng>> = _searchResults

    init {
        observeSearchQuery()
    }

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .collectLatest { query ->
                    val results = geocodeLocation(query)
                    _searchResults.value = results
                }
        }
    }

    private fun geocodeLocation(query: String): List<LatLng> {
//        val geocoder = Geocoder(App.context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocationName(query, 5)
            addresses?.mapNotNull { address ->
                address?.let { LatLng(it.latitude, it.longitude) }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class MapFactory(private val geocoder: Geocoder) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(geocoder) as T
    }

}
