package eg.iti.mad.climaguard.local

import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationsLocalDataSource {
    suspend fun getAllLocations(): Flow<List<LocationEntity>>
    suspend fun insertLocation(location: LocationEntity): Long
    suspend fun deleteLocation(location: LocationEntity): Int
}