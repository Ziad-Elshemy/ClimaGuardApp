package eg.iti.mad.climaguard.api

import eg.iti.mad.climaguard.model.CurrentResponse

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(lat:Double, long:Double):CurrentResponse
}