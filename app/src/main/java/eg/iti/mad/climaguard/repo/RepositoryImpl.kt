package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.api.WeatherRemoteDataSource
import eg.iti.mad.climaguard.local.LocationsLocalDataSource
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow

class RepositoryImpl private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val locationsLocalDataSource: LocationsLocalDataSource
): Repository {

    //remote
    override suspend fun getCurrentWeather(lat:Double, long:Double, units:String, language:String): Flow<CurrentResponse> {
        return weatherRemoteDataSource.getCurrentWeather(lat = lat, long = long, units = units, language = language)
    }

    override suspend fun getForecastWeather(lat: Double, long: Double, units:String, language:String): Flow<ForecastResponse> {
        return weatherRemoteDataSource.getForecastWeather(lat = lat, long = long, units = units, language = language)
    }

    override suspend fun getSearchGeocode(query: String): Flow<List<LocationEntity>> {
        return weatherRemoteDataSource.getSearchGeocode(query)
    }

    //local
    override suspend fun getAllLocations(): Flow<List<LocationEntity>> {
        return locationsLocalDataSource.getAllLocations()
    }

    override suspend fun addLocation(location: LocationEntity): Long {
        return locationsLocalDataSource.insertLocation(location)
    }

    override suspend fun removeLocation(location: LocationEntity): Int {
        return locationsLocalDataSource.deleteLocation(location)
    }


    companion object {
        private var INSTANCE: RepositoryImpl? = null
        fun getInstance(remoteDataSource: WeatherRemoteDataSource, localDataSource: LocationsLocalDataSource): RepositoryImpl{
            return INSTANCE ?: synchronized(this){
                val instance = RepositoryImpl(remoteDataSource,localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }

}