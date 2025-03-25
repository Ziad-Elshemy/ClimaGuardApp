package eg.iti.mad.climaguard.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class LocationHelper(private val context: Context, private val fusedLocationProviderClient: FusedLocationProviderClient) {

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState

    private val _addressState = MutableStateFlow("")
    val addressState: StateFlow<String> = _addressState

    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        if (!isLocationEnabled()) {
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        val locationRequest = LocationRequest.Builder(10000).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            setMinUpdateIntervalMillis(5000)
        }.build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(currentLocation: LocationResult) {
                    super.onLocationResult(currentLocation)
                    val lastLocation = currentLocation.lastLocation ?: return
                    _locationState.value = lastLocation

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addressList: MutableList<Address>) {
                                if (addressList.isNotEmpty()) {
                                    val address = addressList[0]
                                    _addressState.value = "${address.countryName}, ${address.adminArea}, ${address.subAdminArea}"
                                }
                            }
                        })
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}