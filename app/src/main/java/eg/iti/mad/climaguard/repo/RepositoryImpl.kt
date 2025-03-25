package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.api.WeatherRemoteDataSource
import eg.iti.mad.climaguard.model.CurrentResponse
import kotlinx.coroutines.flow.Flow

class RepositoryImpl private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource
): Repository {
    override suspend fun getCurrentWeather(lat:Double, long:Double): Flow<CurrentResponse> {
        return weatherRemoteDataSource.getCurrentWeather(lat,long)
    }








    companion object {
        private var INSTANCE: RepositoryImpl? = null
        fun getInstance(remoteDataSource: WeatherRemoteDataSource/*, localDataSource: WeatherLocalDataSource*/): RepositoryImpl{
            return INSTANCE ?: synchronized(this){
                val instance = RepositoryImpl(remoteDataSource/*,localDataSource*/)
                INSTANCE = instance
                instance
            }
        }
    }

}