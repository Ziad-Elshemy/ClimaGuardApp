package eg.iti.mad.climaguard.api

import com.example.newsapp.api.WebServices
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSourceImpl(private val services: WebServices): WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(lat:Double, long:Double, units:String, language:String): Flow<CurrentResponse> {
        val response = services.getWeather(lat = lat, lon = long, units = units, lang = language)
        return flowOf(response)
    }

    override suspend fun getForecastWeather(lat: Double, long: Double, units:String, language:String): Flow<ForecastResponse> {
        val response = services.getForecast(lat = lat, lon = long, units = units, lang = language)
        return flowOf(response)
    }

    override suspend fun getSearchGeocode(query: String): Flow<List<LocationEntity>> {
        val response = services.getLocations(query = query)
        return flowOf(response)
    }
}