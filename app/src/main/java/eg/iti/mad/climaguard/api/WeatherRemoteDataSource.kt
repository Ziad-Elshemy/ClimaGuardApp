package eg.iti.mad.climaguard.api

import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(lat:Double, long:Double, units:String="metric", language:String="en"): Flow<CurrentResponse>
    suspend fun getForecastWeather(lat:Double, long:Double, units:String="metric", language:String="en"): Flow<ForecastResponse>
    suspend fun getSearchGeocode(query :String): Flow<List<LocationEntity>>
}