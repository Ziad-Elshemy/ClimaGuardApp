package eg.iti.mad.climaguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.api.WeatherRemoteDataSourceImpl
import eg.iti.mad.climaguard.cart.CartScreen
import eg.iti.mad.climaguard.googlemaps.MapScreen
import eg.iti.mad.climaguard.home.HomeFactory
import eg.iti.mad.climaguard.home.HomeScreen
import eg.iti.mad.climaguard.home.HomeViewModel
import eg.iti.mad.climaguard.navigation.BottomNavigationBar
import eg.iti.mad.climaguard.navigation.NavigationRoute
import eg.iti.mad.climaguard.profile.ProfileScreen
import eg.iti.mad.climaguard.repo.RepositoryImpl
import eg.iti.mad.climaguard.settings.SettingScreen
import eg.iti.mad.climaguard.ui.theme.ClimaGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            MapScreen()
            ClimaGuardTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->

                    val graph =
                        navController.createGraph(startDestination = NavigationRoute.Home.route) {
                            composable(route = NavigationRoute.Maps.route) {
                                MapScreen()
                            }
                            composable(route = NavigationRoute.Setting.route) {
                                SettingScreen()
                            }
                            composable(route = NavigationRoute.Home.route) {
                                HomeScreen(
                                    ViewModelProvider(
                                        this@MainActivity,
                                        factory = HomeFactory(
                                            RepositoryImpl.getInstance(
                                                WeatherRemoteDataSourceImpl(
                                                    ApiManager.getApis()
                                                )
                                            )
                                        )
                                    ).get(HomeViewModel::class.java)
                                )
                            }
                            composable(route = NavigationRoute.Profile.route) {
                                ProfileScreen(
                                    ViewModelProvider(
                                        this@MainActivity,
                                        factory = HomeFactory(
                                            RepositoryImpl.getInstance(
                                                WeatherRemoteDataSourceImpl(
                                                    ApiManager.getApis()
                                                )
                                            )
                                        )
                                    ).get(HomeViewModel::class.java)
                                )
                            }
                        }
                    NavHost(
                        navController = navController,
                        graph = graph,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }

        }

        // for the testing branch

    }
}
