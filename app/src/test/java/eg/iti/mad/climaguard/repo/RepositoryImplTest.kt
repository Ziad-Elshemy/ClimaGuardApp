package eg.iti.mad.climaguard.repo

import eg.iti.mad.climaguard.api.FakeWeatherRemoteDataSource
import eg.iti.mad.climaguard.api.WeatherRemoteDataSource
import eg.iti.mad.climaguard.local.FakeLocationsLocalDataSource
import eg.iti.mad.climaguard.local.LocationsLocalDataSource
import eg.iti.mad.climaguard.model.City
import eg.iti.mad.climaguard.model.CurrentResponse
import eg.iti.mad.climaguard.model.ForecastResponse
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.model.Main
import eg.iti.mad.climaguard.model.MainCurrent
import eg.iti.mad.climaguard.model.WeatherItem
import eg.iti.mad.climaguard.model.WeatherItemCurrent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class RepositoryImplTest{

    private val localLocations = mutableListOf(
        LocationEntity(name = "Cairo", id = 1, country = "Egypt", lat = 30.0, lon = 29.0),
        LocationEntity(name = "Fayoum", id = 2, country = "Egypt", lat = 31.0, lon = 28.0)
    )


    val currentResponse = CurrentResponse(
        weather = listOf(WeatherItemCurrent("Snow", "Cloudy")),
        main = MainCurrent(temp = 12.0, humidity = 62),
        name = "Cairo",
        cod = 123
    )

    val forecastResponse = ForecastResponse(
        city = City(name = "Banha", country = "Egypt"),
        cod = "1234"
    )


    private lateinit var fakeLocalDataSource: FakeLocationsLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var repository: RepositoryImpl


    @Before
    fun setup(){
        fakeLocalDataSource = FakeLocationsLocalDataSource(localLocations)
        fakeRemoteDataSource = FakeWeatherRemoteDataSource(currentResponse,forecastResponse)
        repository = RepositoryImpl(fakeRemoteDataSource,fakeLocalDataSource)
    }


    //local
    @Test
    fun getAllLocations_returnsTheSameList()= runTest{
        // Given in setUp

        //when
        val result = repository.getAllLocations()

        //Then
        assertThat(result.first(), `is`(localLocations))
    }

    @Test
    fun addLocation_sendLocationWithId1_returns1()= runTest{
        // Given in setUp

        //when
        val result = repository.addLocation(LocationEntity(name = "Cairo", id = 1, country = "Egypt", lat = 30.0, lon = 29.0))

        //Then
        assertThat(result, `is`(1))
    }


    @Test
    fun addLocation_sendLocationWithId2_returns2()= runTest{
        // Given in setUp

        //when
        val result = repository.addLocation(LocationEntity(name = "Cairo", id = 2, country = "Egypt", lat = 30.0, lon = 29.0))

        //Then
        assertThat(result, `is`(2))
    }

    @Test
    fun deleteLocation_deleteAnyLocation_returns1()= runTest{
        // Given
        val locationEntity = LocationEntity(name = "Cairo", id = 3, country = "Egypt", lat = 30.0, lon = 29.0)


        //when
        repository.addLocation(locationEntity)
        val result = repository.removeLocation(locationEntity)

        //Then
        assertThat(result, `is`(1))
    }





    // remote
    @Test
    fun getCurrentWeather_returnsTheSameObject() = runTest {
        // Given in setUp

        //when
        val result = repository.getCurrentWeather(30.0, 29.0, "metric", "en")

        //Then
        assertThat(result.first(), `is`(currentResponse))
    }

    @Test
    fun getForecastWeather_returnsTheSameObject() = runTest {
        // Given in setUp

        //when
        val result = repository.getForecastWeather(31.0, 28.0, "metric", "en")

        //Then
        assertThat(result.first(), `is`(forecastResponse))
    }



}