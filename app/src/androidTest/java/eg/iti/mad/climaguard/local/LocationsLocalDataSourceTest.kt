package eg.iti.mad.climaguard.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import eg.iti.mad.climaguard.model.LocationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocationsLocalDataSourceTest{

    private lateinit var database: MyDatabase
    private lateinit var dao: LocationDao
    private lateinit var alarmDao: AlarmDao
    private lateinit var localDataSource: LocationsLocalDataSourceImpl
    private lateinit var gizaLocation: LocationEntity
    private lateinit var cairoLocation: LocationEntity

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.locationDao()
        alarmDao = database.alarmDao()
        localDataSource = LocationsLocalDataSourceImpl(dao,alarmDao)

        gizaLocation = LocationEntity(
            id = 101,
            name = "Giza",
            country = "Egypt",
            lat = 30.30,
            lon = 29.29
        )
        cairoLocation = LocationEntity(
            id = 102,
            name = "Cairo",
            country = "Egypt",
            lat = 31.31,
            lon = 28.28
        )

    }

    @After
    fun tearDown() = database.close()


    @Test
    fun insertLocation_insertLocation_retrieveSameLocation() = runTest {
        //Given in setup

        //when
        localDataSource.insertLocation(gizaLocation)
        val result = localDataSource.getAllLocations().first()

        //then
        assertThat(result.contains(gizaLocation), `is`(true))
        assertThat(result[0], `is`(gizaLocation))
    }

    @Test
    fun getAllLocations_insertTwoLocation_returnedSizeIs2() = runTest {
        //Given

        //when
        localDataSource.insertLocation(gizaLocation)
        localDataSource.insertLocation(cairoLocation)
        val result = localDataSource.getAllLocations().first()

        //then
        assertThat(result.size, `is`(2))
    }

    @Test
    fun deleteLocation_insertTwoLocationThenDeleteOne_returnedSizeIs1() = runTest {
        //Given in setup

        //when
        localDataSource.insertLocation(gizaLocation)
        localDataSource.insertLocation(cairoLocation)
        localDataSource.deleteLocation(cairoLocation)

        val result = localDataSource.getAllLocations().first()

        //then
        assertThat(result.size, `is`(1))
    }
    

}
