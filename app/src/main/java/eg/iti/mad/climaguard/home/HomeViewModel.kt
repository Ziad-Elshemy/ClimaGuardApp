package eg.iti.mad.climaguard.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _currentWeather = MutableStateFlow<Response<CurrentResponse>>(Response.Loading)
    val currentResponse = _currentWeather.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()


    fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                //the view model must get the lat and long from GPS
                val response = repo.getCurrentWeather(29.39, 30.87)
                response
                    .catch { ex ->
                        _currentWeather.value = Response.Failure(ex)
                        _toastMessage.emit("Error From Api ${ex.message}")
                    }
                    .collect {data->
                        _currentWeather.value = Response.Success(data)
                    }
            } catch (ex: Exception) {
                _toastMessage.emit("Error from coroutines ${ex.message}")
                Log.d(TAG, "getCurrentWeather: Error catch ${ex.message}")
            }

        }
    }

}

class HomeFactory(private val repo: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }

}