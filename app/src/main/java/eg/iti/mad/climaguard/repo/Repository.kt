package eg.iti.mad.climaguard.repo

import android.location.Location
import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

interface Repository {

    // remote
    suspend fun getCurrentWeather(lat:Double, long:Double, units:String="metric", language:String="en"): Flow<CurrentResponse>
    suspend fun getForecastWeather(lat:Double, long:Double, units:String="metric", language:String="en"): Flow<ForecastResponse>
    suspend fun getSearchGeocode(query :String): Flow<List<LocationEntity>>

    //local locations
    suspend fun getAllLocations(): Flow<List<LocationEntity>?>
    suspend fun addLocation(location: LocationEntity): Long
    suspend fun removeLocation(location: LocationEntity): Int

    //local alarms
    suspend fun getAllAlarms(): Flow<List<AlarmEntity>?>
    suspend fun addAlarm(alarm: AlarmEntity): Long
    suspend fun removeAlarm(alarm: AlarmEntity): Int

}