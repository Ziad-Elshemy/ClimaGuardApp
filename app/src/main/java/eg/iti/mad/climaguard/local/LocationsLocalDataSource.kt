package eg.iti.mad.climaguard.local

import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationsLocalDataSource {
    // locations
    suspend fun getAllLocations(): Flow<List<LocationEntity>>
    suspend fun insertLocation(location: LocationEntity): Long
    suspend fun deleteLocation(location: LocationEntity): Int

    // alarms
    suspend fun getAllAlarms(): Flow<List<AlarmEntity>>
    suspend fun insertAlarm(alarm: AlarmEntity): Long
    suspend fun deleteAlarm(alarm: AlarmEntity): Int
}