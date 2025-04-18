package eg.iti.mad.climaguard

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.NotificationManager
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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.newsapp.api.ApiManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import eg.iti.mad.climaguard.alarm.AlarmFactory
import eg.iti.mad.climaguard.alarm.AlarmScreen
import eg.iti.mad.climaguard.alarm.AlarmViewModel
import eg.iti.mad.climaguard.api.WeatherRemoteDataSourceImpl
import eg.iti.mad.climaguard.favitemscreen.FavItemScreen
import eg.iti.mad.climaguard.favorite.FavoriteFactory
import eg.iti.mad.climaguard.favorite.FavoriteScreen
import eg.iti.mad.climaguard.favorite.FavoriteViewModel
import eg.iti.mad.climaguard.map.MapFactory
import eg.iti.mad.climaguard.map.MapScreen
import eg.iti.mad.climaguard.map.MapViewModel
import eg.iti.mad.climaguard.home.HomeFactory
import eg.iti.mad.climaguard.home.HomeScreen
import eg.iti.mad.climaguard.home.HomeViewModel
import eg.iti.mad.climaguard.local.LocationsLocalDataSourceImpl
import eg.iti.mad.climaguard.local.MyDatabase
import eg.iti.mad.climaguard.navigation.BottomNavigationBar
import eg.iti.mad.climaguard.navigation.NavigationRoute
import eg.iti.mad.climaguard.repo.Repository
import eg.iti.mad.climaguard.repo.RepositoryImpl
import eg.iti.mad.climaguard.settings.SettingsDataStore
import eg.iti.mad.climaguard.settings.SettingsFactory
import eg.iti.mad.climaguard.settings.SettingsScreen
import eg.iti.mad.climaguard.settings.SettingsViewModel
import eg.iti.mad.climaguard.ui.theme.ClimaGuardTheme
import eg.iti.mad.climaguard.utils.LocationPreferences
import eg.iti.mad.climaguard.utils.Utility.Companion.setAppLocale
import eg.iti.mad.climaguard.worker.SoundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    var address : MutableState<String> = mutableStateOf("")
    val REQUEST_LOCATION_CODE = 101
    lateinit var geocoder : Geocoder
    lateinit var settingsDataStore: SettingsDataStore
    lateinit var repo :Repository
    lateinit var locationPrefs: LocationPreferences

    private val _locationState = MutableStateFlow(Location(LocationManager.GPS_PROVIDER))
    val locationState: StateFlow<Location> = _locationState.asStateFlow()

    var isGpsEnabled by mutableStateOf(true)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        geocoder = Geocoder(this)
        settingsDataStore = SettingsDataStore(this)
        locationPrefs = LocationPreferences(this)

        lifecycleScope.launch {
            val language = settingsDataStore.language.first()
            setAppLocale(this@MainActivity, language)
        }

        repo = RepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl(
                ApiManager.getApis()
            ),LocationsLocalDataSourceImpl(
                MyDatabase.getInstance(this@MainActivity).locationDao(),
                MyDatabase.getInstance(this@MainActivity).alarmDao()
            )
        )

        val homeViewModel = ViewModelProvider(
            this@MainActivity,
            factory = HomeFactory(
                repo
                ,settingsDataStore
            )

        ).get(HomeViewModel::class.java)

        val alarmViewModel = ViewModelProvider(
            this@MainActivity,
            factory = AlarmFactory(
                repo
            )
        ).get(AlarmViewModel::class.java)

        val favoriteViewModel = ViewModelProvider(
            this,
            factory = FavoriteFactory(
                repo)
        ).get(FavoriteViewModel::class.java)

        val mapViewModel = ViewModelProvider(
            this,
            factory = MapFactory(
                repo)
        ).get(MapViewModel::class.java)

        val settingsViewModel = ViewModelProvider(
            this,
            factory = SettingsFactory(
                settingsDataStore
            )
        ).get(SettingsViewModel::class.java)



//        splashScreen.setKeepOnScreenCondition {
//            // false to hide
//            false
//        }
//
//        splashScreen.setOnExitAnimationListener { splashView ->
//            // do animation here
//            splashView.remove()
//        }

        setContent {

            val navController = rememberNavController()
//
//            val showSplash = remember { mutableStateOf(true) }
//            LaunchedEffect(Unit) {
//                delay(2000)
//                showSplash.value = false
//            }

            LaunchedEffect(Unit) {
                settingsDataStore.gpsEnabled.collect {
                    isGpsEnabled = it
                }
            }

            val targetScreen = intent?.getStringExtra("TARGET_SCREEN")
            val notificationId = intent?.getIntExtra("NOTIFICATION_ID", -1)
            val dateTime = intent?.getLongExtra("DATE_TIME", -1)
            val targetLat = intent?.getDoubleExtra("LAT",30.0)
            val targetLon = intent?.getDoubleExtra("LON",29.0)

            LaunchedEffect(targetScreen) {
                if (targetScreen != null && targetScreen == "home") {
                    if (targetLat != null && targetLon !=null) {
                        navController.navigate(NavigationRoute.FavItem.createRoute(targetLat, targetLon))
                    }
                    // delete notification from database
                    if (dateTime != null && dateTime != -1L) {
                        CoroutineScope(Dispatchers.IO).launch {
                            repo.removeAlarmById(dateTime)
                        }
                    }

                    // delete notification from app bar
                    if (notificationId != null && notificationId != -1) {
                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(notificationId)
                    }

                    stopService(Intent(this@MainActivity, SoundService::class.java))

                }
            }

            val language = runBlocking { settingsDataStore.language.first() }
            val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
//                locationState = remember { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }
//            MapScreen()
                ClimaGuardTheme {
//                    val navController = rememberNavController()

//                    if (showSplash.value) {
//                        SplashScreenContent()
//                    } else {

                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize(),
                            bottomBar = { BottomNavigationBar(navController) }
                        ) { innerPadding ->

                            val gpsLocation = locationState.collectAsState().value
                            val currentLocation: Location = if (isGpsEnabled) {
                                gpsLocation
                            } else {
                                Location("").apply {
                                    latitude = locationPrefs.getLocation().first
                                    longitude = locationPrefs.getLocation().second
                                }
                            }

                            val graph =
                                navController.createGraph(startDestination = NavigationRoute.Home.route) {
                                    composable(route = NavigationRoute.Maps.route) { backStackEntry ->
                                        val screenType =
                                            backStackEntry.arguments?.getString("screenType")
                                                ?: "favorite"
                                        MapScreen(
                                            mapViewModel, currentLocation,
                                            screenType = screenType,
                                            navController
                                        )
                                    }
                                    composable(route = NavigationRoute.FavItem.route) { backStackEntry ->
                                        val lat = backStackEntry.arguments?.getString("lat")
                                            ?.toDoubleOrNull() ?: 0.0
                                        val lon = backStackEntry.arguments?.getString("lon")
                                            ?.toDoubleOrNull() ?: 0.0

                                        val location = Location("").apply {
                                            latitude = lat
                                            longitude = lon
                                        }

                                        FavItemScreen(homeViewModel, location)
                                    }
                                    composable(route = NavigationRoute.Favorite.route) {
                                        FavoriteScreen(navController, favoriteViewModel)
                                    }
                                    composable(route = NavigationRoute.Setting.route) {
                                        SettingsScreen(navController, settingsViewModel)
                                    }
                                    composable(route = NavigationRoute.Home.route) {
                                        if (currentLocation.latitude != 0.0) {
                                            HomeScreen(
                                                homeViewModel,
                                                currentLocation
                                            )
                                        }

                                    }
                                    composable(route = NavigationRoute.Alarm.route) {
                                        AlarmScreen(
                                            navController,
                                            alarmViewModel
                                        )
                                    }
                                }
                            NavHost(
                                navController = navController,
                                graph = graph,
                                modifier = Modifier.padding(innerPadding)
                            )

                        }
//                    }
                }
            }



        }

        // for the testing branch

    }

    @Composable
    fun SplashScreenContent() {

        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.splash_animation))

        LottieAnimation(
            composition = composition,
            modifier = Modifier.fillMaxSize(),
            iterations = LottieConstants.IterateForever
        )
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

                    val newLocation = currentLocation.lastLocation ?: Location(LocationManager.GPS_PROVIDER)

                    //
                    if (_locationState.value.latitude != newLocation.latitude ||
                        _locationState.value.longitude != newLocation.longitude) {

                        _locationState.value = newLocation

                        Log.i(TAG, "onLocationResult: lat = ${newLocation.latitude}, long = ${newLocation.longitude}")

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(
                                newLocation.latitude, newLocation.longitude, 1,
                                object : GeocodeListener {
                                    override fun onGeocode(addressList: MutableList<Address>) {
                                        address.value = addressList[0].countryName + ", " +
                                                addressList[0].adminArea + ", " +
                                                addressList[0].subAdminArea
                                        Log.d(TAG, "onGeocode: ${address.value}")
                                    }
                                }
                            )
                        }
                    }
                }

            },
            Looper.myLooper()
        )
    }


}
