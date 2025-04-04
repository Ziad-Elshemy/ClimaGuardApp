package eg.iti.mad.climaguard.local

import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeLocationsLocalDataSource(
    val locationsList :MutableList<LocationEntity> = mutableListOf()
) :LocationsLocalDataSource {
    override suspend fun getAllLocations(): Flow<List<LocationEntity>> {
            return flowOf(locationsList)
    }

    override suspend fun insertLocation(location: LocationEntity): Long {
        try {
            locationsList.add(location)
            return location.id.toLong()
        }catch (ex:Exception){
            return -1
        }

    }

    override suspend fun deleteLocation(location: LocationEntity): Int {
        try {
            locationsList.remove(location)
            return 1
        }catch (ex:Exception){
            return -1
        }
    }

    override suspend fun getAllAlarms(): Flow<List<AlarmEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: AlarmEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarmById(alarmId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarmByUUId(uuId: String): Int {
        TODO("Not yet implemented")
    }
}