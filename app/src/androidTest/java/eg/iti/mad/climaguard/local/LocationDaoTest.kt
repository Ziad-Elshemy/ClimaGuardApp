package eg.iti.mad.climaguard.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.iti.mad.climaguard.model.LocationEntity
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class LocationDaoTest {

    private lateinit var dao: LocationDao
    private lateinit var database: MyDatabase
    private lateinit var gizaLocation: LocationEntity
    private lateinit var cairoLocation: LocationEntity


    @Before
    fun setup(){

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabase::class.java
        ).build()

        dao = database.locationDao()

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
    fun getLocationById_insertLocation_returnSameLocation() = runTest{
        //Given

        dao.insertLocation(gizaLocation)

        //when
        val result = dao.getLocationById(101)

        //then
        assertNotNull(result)
        result as LocationEntity
        assertThat(result.id, `is`(gizaLocation.id))
        assertThat(result.name, `is`(gizaLocation.name))
        assertThat(result. country, `is`(gizaLocation.country))
        assertThat(result.lat, `is`(gizaLocation.lat))
        assertThat(result.lon, `is`(gizaLocation.lon))

    }

    @Test
    fun insertLocation_addLocation_returnTrue() = runTest {
        //Given
        dao.insertLocation(cairoLocation)

        //When
        val result = dao.getAllLocations().first()

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result.contains(cairoLocation), `is`(true))
    }

    @Test
    fun insertLocation_addTwoLocations_returnSize2() = runTest {
        //Given
        dao.insertLocation(cairoLocation)
        dao.insertLocation(gizaLocation)

        //When
        val result = dao.getAllLocations().first()

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result.size , `is`(2))

    }


    @Test
    fun deleteLocation_addTwoLocationsThenDeleteOne_returnSize1() = runTest {
        //Given
        dao.insertLocation(cairoLocation)
        dao.insertLocation(gizaLocation)

        //When
        dao.deleteLocation(gizaLocation)
        val result = dao.getAllLocations().first()

        //Then
        assertThat(result, not(nullValue()))
        assertThat(result.size , `is`(1))

    }


}