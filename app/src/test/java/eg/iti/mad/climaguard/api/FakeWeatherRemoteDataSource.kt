package eg.iti.mad.climaguard.api

import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherRemoteDataSource(
    private val currentResponse: CurrentResponse,
    private val forecastResponse: ForecastResponse
) :WeatherRemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        long: Double,
        units: String,
        language: String
    ): Flow<CurrentResponse> {
        return flowOf(currentResponse)
    }

    override suspend fun getForecastWeather(
        lat: Double,
        long: Double,
        units: String,
        language: String
    ): Flow<ForecastResponse> {
        return flowOf(forecastResponse)
    }

    override suspend fun getSearchGeocode(query: String): Flow<List<LocationEntity>> {
        TODO("Not yet implemented")
    }
}