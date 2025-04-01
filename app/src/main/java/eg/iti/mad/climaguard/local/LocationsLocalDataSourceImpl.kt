package eg.iti.mad.climaguard.local

import eg.iti.mad.climaguard.model.AlarmEntity
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

class LocationsLocalDataSourceImpl(private val dao: LocationDao, private val alarmsDao: AlarmDao) :LocationsLocalDataSource {
    override suspend fun getAllLocations(): Flow<List<LocationEntity>> {
        return dao.getAllLocations()
    }

    override suspend fun insertLocation(location: LocationEntity): Long {
        return dao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: LocationEntity): Int {
        return dao.deleteLocation(location)
    }

    override suspend fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alarmsDao.getAllAlarms()
    }

    override suspend fun insertAlarm(alarm: AlarmEntity): Long {
        return alarmsDao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity): Int {
        return alarmsDao.deleteAlarm(alarm)
    }

}