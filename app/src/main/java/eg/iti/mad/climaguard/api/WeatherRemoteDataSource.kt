package eg.iti.mad.climaguard.api

import eg.iti.mad.climaguard.model.CurrentResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(lat:Double, long:Double): Flow<CurrentResponse>
}