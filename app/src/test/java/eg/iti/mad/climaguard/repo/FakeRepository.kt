package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeRepository:Repository {

    private val locationsListFlow = MutableStateFlow<List<LocationEntity>>(emptyList())


    override suspend fun getCurrentWeather(
        lat: Double,
        long: Double,
        units: String,
        language: String
    ): Flow<CurrentResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastWeather(
        lat: Double,
        long: Double,
        units: String,
        language: String
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchGeocode(query: String): Flow<List<LocationEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllLocations(): Flow<List<LocationEntity>?> {
        return locationsListFlow
    }

    override suspend fun addLocation(location: LocationEntity): Long {
        return try {
            locationsListFlow.update { it + location }
            1
        } catch (ex: Exception) {
            -1
        }
    }

    override suspend fun removeLocation(location: LocationEntity): Int {
        return try {
            locationsListFlow.update { it - location }
            1
        } catch (ex: Exception) {
            -1
        }

    }

    override suspend fun getAllAlarms(): Flow<List<AlarmEntity>?> {
        TODO("Not yet implemented")
    }

    override suspend fun addAlarm(alarm: AlarmEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeAlarm(alarm: AlarmEntity): Int {
        TODO("Not yet implemented")
    }

    override suspend fun removeAlarmById(alarmId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun removeAlarmByUUId(uuId: String): Int {
        TODO("Not yet implemented")
    }
}