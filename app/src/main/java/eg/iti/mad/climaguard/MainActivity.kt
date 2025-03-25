package eg.iti.mad.climaguard

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.example.newsapp.api.ApiManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import eg.iti.mad.climaguard.api.WeatherRemoteDataSourceImpl
import eg.iti.mad.climaguard.favorite.FavoriteScreen
import eg.iti.mad.climaguard.map.MapFactory
import eg.iti.mad.climaguard.map.MapScreen
import eg.iti.mad.climaguard.map.MapViewModel
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
    private val TAG = "MainActivity"
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    lateinit var locationState : MutableState<Location>
    var address : MutableState<String> = mutableStateOf("")
    val REQUEST_LOCATION_CODE = 101
    lateinit var geocoder : Geocoder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        geocoder = Geocoder(this)

        val homeViewModel = ViewModelProvider(
            this@MainActivity,
            factory = HomeFactory(
                RepositoryImpl.getInstance(
                    WeatherRemoteDataSourceImpl(
                        ApiManager.getApis()
                    )
                )
            )
        ).get(HomeViewModel::class.java)

        val mapViewModel = ViewModelProvider(
            this,
            factory = MapFactory(geocoder)
        ).get(MapViewModel::class.java)


        setContent {
            locationState = remember { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }
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
                                MapScreen(
                                    mapViewModel
                                    , locationState.value
                                )
                            }
                            composable(route = NavigationRoute.Favorite.route) {
                                FavoriteScreen(navController)
                            }
                            composable(route = NavigationRoute.Setting.route) {
                                SettingScreen()
                            }
                            composable(route = NavigationRoute.Home.route) {
                                HomeScreen(
                                    homeViewModel,
                                    locationState.value
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_LOCATION_CODE){
            if (grantResults.get(0) == PackageManager.PERMISSION_GRANTED || grantResults.get(1) == PackageManager.PERMISSION_GRANTED){
                if (isMyLocationEnabled()){
                    getFreshLocation()
                }else{
                    enableLocationServices()
                }
            }else {
                requestPermissionsFromUser()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkPermissions(): Boolean {
        var result = false
        if ((ContextCompat.checkSelfPermission (this ,
                ACCESS_COARSE_LOCATION ) == PackageManager. PERMISSION_GRANTED )
            ||
            (ContextCompat.checkSelfPermission (this ,
                ACCESS_FINE_LOCATION
            ) == PackageManager. PERMISSION_GRANTED ))
            result = true
        return result
    }

    private fun isMyLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()){
            if (isMyLocationEnabled()){
                getFreshLocation()
            }else{
                enableLocationServices()
            }
        }
        else{
            requestPermissionsFromUser()
        }
    }

    private fun requestPermissionsFromUser() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_CODE
        )
    }

    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        // init fused
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(5000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                setMinUpdateIntervalMillis(5000)
            }.build(),
            object : LocationCallback(){
                override fun onLocationResult(currentLocation: LocationResult) {
                    super.onLocationResult(currentLocation)
                    locationState.value = currentLocation.lastLocation?: Location(LocationManager.GPS_PROVIDER)
                    Log.i(TAG, "onLocationResult: lat = ${locationState.value.latitude}  long = ${locationState.value.longitude}")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(locationState.value.latitude,locationState.value.longitude,1, object :
                            GeocodeListener {
                            override fun onGeocode(addressList: MutableList<Address>) {
                                address.value = addressList[0].countryName + ", " + addressList[0].adminArea + ", " + addressList[0].subAdminArea
                                Log.d(TAG, "onGeocode: ${address.value}")
                            }
                        })
                    }

                }
            },
            Looper.myLooper()
        )
    }


}
