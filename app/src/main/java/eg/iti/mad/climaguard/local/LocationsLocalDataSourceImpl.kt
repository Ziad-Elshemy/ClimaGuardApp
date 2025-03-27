package eg.iti.mad.climaguard.local

import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

class LocationsLocalDataSourceImpl(private val dao: LocationDao) :LocationsLocalDataSource {
    override suspend fun getAllLocations(): Flow<List<LocationEntity>> {
        return dao.getAllLocations()
    }

    override suspend fun insertLocation(location: LocationEntity): Long {
        return dao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: LocationEntity): Int {
        return dao.deleteLocation(location)
    }
}