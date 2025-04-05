package eg.iti.mad.climaguard.favorite

import androidx.test.ext.junit.runners.AndroidJUnit4
import eg.iti.mad.climaguard.model.LocationEntity
import eg.iti.mad.climaguard.model.Response
import eg.iti.mad.climaguard.repo.FakeRepository
import eg.iti.mad.climaguard.repo.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest{

    private lateinit var repo: Repository
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var gizaLocation: LocationEntity
    private lateinit var cairoLocation: LocationEntity

    @Before
    fun setup(){
        repo = FakeRepository()
        favoriteViewModel = FavoriteViewModel(repo)

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


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertLocation_addLocation_returnSizeIs1AndContainSameObject() = runTest {
        // Given in setup

        // When
        repo.addLocation(gizaLocation)
        advanceUntilIdle()
        favoriteViewModel.getFavorites()
        advanceUntilIdle()

        // Then
        val locationsState = favoriteViewModel.locationsList.first { it is Response.Success } as Response.Success

        assertThat(locationsState.data.size, `is`(1))
        assertThat(locationsState.data.contains(gizaLocation), `is`(true))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertLocation_add2Locationس_returnSizeIs2() = runTest {
        // Given in setup

        // When
        repo.addLocation(gizaLocation)
        advanceUntilIdle()
        repo.addLocation(cairoLocation)
        advanceUntilIdle()
        favoriteViewModel.getFavorites()
        advanceUntilIdle()

        // Then
        val locationsState = favoriteViewModel.locationsList.first { it is Response.Success } as Response.Success

        assertThat(locationsState.data.size, `is`(2))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteLocation_deleteLocationFromFavorites_returnSizeIs0AndLocationNotPresent() = runTest {
        // Given in setup
        repo.addLocation(gizaLocation)
        advanceUntilIdle()
        favoriteViewModel.getFavorites()
        advanceUntilIdle()

        // When: deleting the location
        favoriteViewModel.deleteLocationFromFav(gizaLocation)
        advanceUntilIdle()
        favoriteViewModel.getFavorites()
        advanceUntilIdle()

        // Then
        val locationsState = favoriteViewModel.locationsList.first { it is Response.Success } as Response.Success

        assertThat(locationsState.data.size, `is`(0))
        assertThat(locationsState.data.contains(gizaLocation), `is`(false))
    }



}