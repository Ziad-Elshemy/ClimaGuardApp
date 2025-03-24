package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.model.CurrentResponse
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getCurrentWeather(lat:Double, long:Double): Flow<CurrentResponse>
}