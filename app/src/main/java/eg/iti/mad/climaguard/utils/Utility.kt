package eg.iti.mad.climaguard.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utility {


    companion object {

        fun setAppLocale(context: Context, language: String) {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val config = context.resources.configuration
            config.setLocale(locale)
            config.setLayoutDirection(locale)

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }



        fun formatTimestamp(timestamp: Long): Pair<String, String> {
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            return Pair(dateFormat.format(date), timeFormat.format(date))
        }

        fun getDayOfWeek(timestamp: Long): String {
            val date = Date(timestamp)
            val today = Date()

            val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            return if (dateOnlyFormat.format(date) == dateOnlyFormat.format(today)) {
                "Today"
            } else {
                dateFormat.format(date)
            }
        }

        fun getTimeDay(timestamp: Long): String {
            val date = Date(timestamp)

            val timeFormat = SimpleDateFormat("hh a", Locale.getDefault())

            return timeFormat.format(date)
        }


        fun isDayTime(timestamp: Long): Boolean {
            val date = Date(timestamp)
            val timeFormat = SimpleDateFormat("HH", Locale.getDefault())
            val hour = timeFormat.format(date).toInt()

            return hour in 6..17
        }

        fun calculateAltitude(seaLevelPressure: Double, groundPressure: Double): Int {
            return (44330 * (1 - Math.pow(groundPressure / seaLevelPressure, 0.1903))).toInt()
        }

    }

}