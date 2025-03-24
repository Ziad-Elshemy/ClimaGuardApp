package eg.iti.mad.climaguard.api

import com.example.newsapp.api.WebServices
import eg.iti.mad.climaguard.model.CurrentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSourceImpl(private val services: WebServices): WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(lat:Double, long:Double): Flow<CurrentResponse> {
        val response = services.getWeather(lat,long)
        return flowOf(response)
    }
}