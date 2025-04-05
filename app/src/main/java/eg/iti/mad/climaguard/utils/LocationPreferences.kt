package eg.iti.mad.climaguard.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class LocationPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val LAT_KEY = "lat"
        private const val LON_KEY = "lon"
    }

    fun saveLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat(LAT_KEY, lat.toFloat())
            .putFloat(LON_KEY, lon.toFloat())
            .apply()
        Log.d("LocationPreferences", "saveLocation: $lat and $lon saved")
    }

    fun getLocation(): Pair<Double, Double> {
        val lat = prefs.getFloat(LAT_KEY, 0.0f).toDouble()
        val lon = prefs.getFloat(LON_KEY, 0.0f).toDouble()
        return Pair(lat, lon)
    }

    fun clearLocation() {
        prefs.edit().clear().apply()
    }
}
