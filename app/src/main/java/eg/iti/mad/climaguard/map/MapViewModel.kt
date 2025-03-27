package eg.iti.mad.climaguard.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(private val repo: Repository) : ViewModel() {
    private val _searchQuery = MutableSharedFlow<String>()
    val searchQuery = _searchQuery.asSharedFlow()

    private val _searchResults = MutableStateFlow<List<LatLng>>(emptyList())
    val searchResults: StateFlow<List<LatLng>> = _searchResults

    private val _searchSuggestions = MutableStateFlow<List<LocationEntity>>(emptyList())
    val searchSuggestions: StateFlow<List<LocationEntity>> = _searchSuggestions

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

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
                .distinctUntilChanged()
                .collectLatest { query ->
                    val locations = getLocationsFromAPI(query)
                    _searchSuggestions.value = locations // Display suggestions
                }
        }
    }

    fun updateSelectedLocation(location: LocationEntity) {
        _selectedLocation.value = LatLng(location.lat, location.lon)
        _searchResults.value = listOf(_selectedLocation.value!!) // Move camera & marker
    }

    private suspend fun getLocationsFromAPI(query: String): List<LocationEntity> {
        return try {
            withContext(Dispatchers.IO) {
                repo.getSearchGeocode(query).firstOrNull() ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class MapFactory(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(repo) as T
    }
}
