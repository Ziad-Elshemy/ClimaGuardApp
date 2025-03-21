package eg.iti.mad.climaguard.api

import com.example.newsapp.api.WebServices
import eg.iti.mad.climaguard.model.CurrentResponse

class WeatherRemoteDataSourceImpl(private val services: WebServices): WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(lat:Double, long:Double): CurrentResponse {
        return services.getWeather(lat,long)
    }
}