package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getCurrentWeather(lat:Double, long:Double): Flow<CurrentResponse>
    suspend fun getForecastWeather(lat:Double, long:Double): Flow<ForecastResponse>
}