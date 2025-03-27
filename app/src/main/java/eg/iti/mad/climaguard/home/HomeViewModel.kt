package eg.iti.mad.climaguard.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.ListItem
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.repo.Repository
import eg.iti.mad.climaguard.settings.SettingsDataStore
import eg.iti.mad.climaguard.utils.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: Repository,
                    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val TAG = "HomeViewModel"

    private val _currentWeather = MutableStateFlow<Response<CurrentResponse>>(Response.Loading)
    val currentResponse = _currentWeather.asStateFlow()

    private val _forecastWeather = MutableStateFlow<Response<ForecastResponse>>(Response.Loading)
    val forecastResponse = _forecastWeather.asStateFlow()

    private val _hourlyForecastWeatherList = MutableStateFlow<List<ListItem?>?>(emptyList())
    val hourlyForecastResponseList = _hourlyForecastWeatherList.asStateFlow()

    private val _fiveDaysForecastWeatherList = MutableStateFlow<List<ListItem?>?>(emptyList())
    val fiveDaysForecastWeatherList = _fiveDaysForecastWeatherList.asStateFlow()

    private val _lastLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val lastLocation = _lastLocation.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()


    fun getCurrentWeather(lat :Double, lon :Double, units:String, language:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                //the view model must get the lat and long from GPS
                val response = repo.getCurrentWeather(lat, lon, units, language)
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


    fun getForecastWeather(lat: Double, lon: Double, units:String, language:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repo.getForecastWeather(lat, lon, units, language)
                response
                    .catch { ex ->
                        _forecastWeather.value = Response.Failure(ex)
                        _toastMessage.emit("Error From API (Forecast) ${ex.message}")
                    }
                    .collect { data ->
                        _forecastWeather.value = Response.Success(data)
                        _hourlyForecastWeatherList.value = data.list

                        val groupedByDay = data.list?.groupBy { it?.dtTxt?.substring(0, 10) ?: "" }

                        val fiveDaysForecast = groupedByDay?.map { (date, items) ->
                            val minTemp = items.minByOrNull { it?.main?.tempMin ?: Double.MAX_VALUE }?.main?.tempMin ?: 0.0
                            val maxTemp = items.maxByOrNull { it?.main?.tempMax ?: Double.MIN_VALUE }?.main?.tempMax ?: 0.0

                            val dayName = Utility.getDayOfWeek(items.first()?.dt?.times(1000L) ?: 0)

                            items.first()?.copy(
                                dtTxt = dayName,
                                main = items.first()!!.main?.copy(tempMin = minTemp, tempMax = maxTemp)
                            )
                        }?.take(5)

                        Log.d(TAG, "getForecastWeather: Processed forecast data: $fiveDaysForecast")

                        _fiveDaysForecastWeatherList.value = fiveDaysForecast
                    }

            } catch (ex: Exception) {
                _toastMessage.emit("Error from coroutines (Forecast) ${ex.message}")
                Log.d(TAG, "getForecastWeather: Error catch ${ex.message}")
            }
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        val newLocation = Pair(lat, lon)
        if (_lastLocation.value == null || _lastLocation.value != newLocation) {
            _lastLocation.value = newLocation
            fetchWeatherData(lat, lon)
        }
    }

    fun fetchWeatherData(lat: Double, lon: Double) {

        viewModelScope.launch {
            val language = settingsDataStore.language.first()
            val tempUnit = settingsDataStore.tempUnit.first()

            getCurrentWeather(lat, lon, tempUnit, language)
            getForecastWeather(lat, lon, tempUnit, language)
        }

    }

}

class HomeFactory(private val repo: Repository,
                  private val settingsDataStore: SettingsDataStore) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo,settingsDataStore) as T
    }

}