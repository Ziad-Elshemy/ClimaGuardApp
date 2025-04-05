package eg.iti.mad.climaguard.map

import android.content.Context
import android.location.Geocoder
import android.util.Log
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
import java.util.Locale

class MapViewModel(private val repo: Repository) : ViewModel() {

    private val TAG = "MapViewModel"

    private val _searchQuery = MutableSharedFlow<String>()
    val searchQuery = _searchQuery.asSharedFlow()

    private val _searchResults = MutableStateFlow<List<LatLng>>(emptyList())
    val searchResults: StateFlow<List<LatLng>> = _searchResults

    private val _searchSuggestions = MutableStateFlow<List<LocationEntity>>(emptyList())
    val searchSuggestions: StateFlow<List<LocationEntity>> = _searchSuggestions

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

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


    fun addLocationToFav(location: LocationEntity){
        viewModelScope.launch(Dispatchers.IO) {
            if (location!=null){
                try {
                    val result = repo.addLocation(location)
                    if (result>0){
                        mutableMessage.emit("${location.name} Added Successfully!")
                        Log.d(TAG, "addLocationToFav: success")
                    }else{
                        mutableMessage.emit("Product is already in Fav")
                        Log.d(TAG, "addLocationToFav: already in Fav")
                    }
                }catch (ex:Exception){
                    mutableMessage.emit("Couldn't added Location :${ex.message}")
                    Log.d(TAG, "addLocationToFav: Couldn't added")
                }

            }else{
                mutableMessage.emit("Couldn't added Location :Missing data")
                Log.d(TAG, "addLocationToFav: :Missing data")
            }
        }
    }

    fun getCityAndCountryFromLocation(lat: Double, lon: Double, context: Context, onResult: (String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: addresses[0].adminArea ?: "Unknown City"
                    val country = addresses[0].countryName ?: "Unknown Country"
                    Log.d(TAG, "getCityAndCountryFromLocation: ${addresses.get(0).toString()}")
                    withContext(Dispatchers.Main) {
                        onResult(city, country)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult("Unknown City", "Unknown Country")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult("Unknown City", "Unknown Country")
                }
            }
        }
    }



}

class MapFactory(private val repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(repo) as T
    }
}
