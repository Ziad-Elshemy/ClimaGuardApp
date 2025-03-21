package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.model.CurrentResponse

interface Repository {
    suspend fun getCurrentWeather(lat:Double, long:Double): CurrentResponse
}