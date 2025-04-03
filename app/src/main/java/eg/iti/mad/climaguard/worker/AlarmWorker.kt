package eg.iti.mad.climaguard.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.MainActivity
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.api.WeatherRemoteDataSourceImpl
import eg.iti.mad.climaguard.local.LocationsLocalDataSourceImpl
import eg.iti.mad.climaguard.local.MyDatabase
import eg.iti.mad.climaguard.notification.NotificationActivity
import eg.iti.mad.climaguard.repo.Repository
import eg.iti.mad.climaguard.repo.RepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.UnknownHostException

class AlarmWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("AlarmWorker", "doWork() started")

        val dateTime = inputData.getLong("DATE_TIME", 0L)
        val type = inputData.getString("TYPE") ?: "Notification"
        val title = inputData.getString("TITLE") ?: "Alarm"
        val message = inputData.getString("MESSAGE") ?: "It's time!"

        val lat = inputData.getDouble("LAT", 0.0)
        val lon = inputData.getDouble("LON", 0.0)

        Log.d("AlarmWorker", "Type: $type, Title: $title, Message: $message, Lat: $lat, Lon: $lon")

        return try {
            val repo: Repository = RepositoryImpl.getInstance(
                WeatherRemoteDataSourceImpl(ApiManager.getApis()),
                LocationsLocalDataSourceImpl(
                    MyDatabase.getInstance(applicationContext).locationDao(),
                    MyDatabase.getInstance(applicationContext).alarmDao()
                )
            )

            //
            val weatherResponse = repo.getCurrentWeather(lat, lon, "metric", "en").first()
            val cityName = weatherResponse.name
            val weatherDescription = weatherResponse.weather?.firstOrNull()?.description ?: "Unknown weather"

            val updatedMessage = "Weather in $cityName: $weatherDescription"

            if (type == "Notification") {
                showNotification(applicationContext, title, updatedMessage,dateTime, lat,lon)
            } else {
                playAlarm(applicationContext, cityName ?: "Unknown City", weatherDescription, dateTime)
            }

            Result.success()

        } catch (ex: UnknownHostException) {
            Log.e("AlarmWorker", "No internet connection, retrying later...")
            Result.retry()

        } catch (ex: Exception) {
            Log.e("AlarmWorker", "Error fetching weather: ${ex.message}")
            Result.failure()
        }
    }

    private fun showNotification(context: Context, title: String, message: String, dateTime: Long, lat: Double, lon: Double) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/raw/alarm")
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val channel = NotificationChannel(channelId, "Alarms", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = dateTime.hashCode()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TARGET_SCREEN", "home")
            putExtra("LAT", lat)
            putExtra("LON", lon)
            putExtra("DATE_TIME", dateTime)
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundServiceIntent = Intent(context, SoundService::class.java)
        context.startService(soundServiceIntent)

        val dismissIntent = Intent(context, DismissReceiver::class.java).apply {
            putExtra("DATE_TIME", dateTime)
            putExtra("NOTIFICATION_ID", notificationId)
        }
        val dismissPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.clock)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(null)
            .addAction(R.drawable.ic_humidity, "Dismiss", dismissPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun playAlarm(context: Context, cityName: String, weatherDescription: String, dateTime: Long) {
        Log.d("AlarmWorker", "Playing alarm sound")

        val intent = Intent(context, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("DATE_TIME", dateTime)
            putExtra("CITY_NAME", cityName)
            putExtra("WEATHER_DESC", weatherDescription)
        }
        context.startActivity(intent)

        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)
        mediaPlayer.start()
    }
}

