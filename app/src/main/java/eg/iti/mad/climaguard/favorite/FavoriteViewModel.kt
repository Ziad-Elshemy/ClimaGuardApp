package eg.iti.mad.climaguard.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: Repository) : ViewModel() {
    private val TAG = "FavoriteViewModel"

    private val _locationsList = MutableStateFlow<Response<List<LocationEntity>>>(Response.Loading)
    val locationsList = _locationsList.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()


    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.getAllLocations()
                result
                    .catch { ex ->
                        _locationsList.value = Response.Failure(ex)
                        _message.emit("Error From DB ${ex.message}")
                    }
                    .collect {
                        _locationsList.value = Response.Success(it!!)
                    }
            } catch (ex: Exception) {

                Log.e(TAG, "FavoriteViewModel: Error ${ex.message}")
                _message.emit("Error from coroutines ${ex.message}")
            }
        }
    }

    fun deleteLocationFromFav(location: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.removeLocation(location)
                if (result > 0) {
                    _message.emit("${location.name} Deleted Successfully!")

                } else {
                    _message.emit("Location is already deleted res: $result")
                }
            } catch (ex: Exception) {
                _message.emit("Couldn't delete Location :${ex.message}")
            }
        }
    }

}

class FavoriteFactory(private val repo: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteViewModel(repo) as T
    }

}
