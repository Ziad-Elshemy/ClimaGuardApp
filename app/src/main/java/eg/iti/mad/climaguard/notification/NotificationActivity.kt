package eg.iti.mad.climaguard.notification

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.R
import eg.iti.mad.climaguard.alarm.AlarmFactory
import eg.iti.mad.climaguard.alarm.AlarmViewModel
import eg.iti.mad.climaguard.api.WeatherRemoteDataSourceImpl
import eg.iti.mad.climaguard.local.LocationsLocalDataSourceImpl
import eg.iti.mad.climaguard.local.MyDatabase
import eg.iti.mad.climaguard.repo.Repository
import eg.iti.mad.climaguard.repo.RepositoryImpl

class NotificationActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var notificationViewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cityName = intent.getStringExtra("CITY_NAME") ?: "Unknown"
        val weatherDesc = intent.getStringExtra("WEATHER_DESC") ?: "No Data"

        val repo: Repository = RepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl(ApiManager.getApis()),
            LocationsLocalDataSourceImpl(
                MyDatabase.getInstance(this).locationDao(),
                MyDatabase.getInstance(this).alarmDao()
            )
        )

        notificationViewModel = ViewModelProvider(
            this,
            AlarmFactory(repo)
        )[AlarmViewModel::class.java]

        startAlarmSound()

        setContent {
            NotificationScreen(
                cityName = cityName,
                weatherDesc = weatherDesc,
                onDismiss = { stopNotification() }
            )
        }
    }

    private fun startAlarmSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm).apply {
                isLooping = true
                start()
            }
        }
    }

    private fun stopNotification() {
        val dateTime = intent.getLongExtra("DATE_TIME", -1L)
        if (dateTime != -1L) {
            notificationViewModel.deleteAlarmById(dateTime)
            Toast.makeText(this, "Notification dismissed", Toast.LENGTH_SHORT).show()
        }
        releaseMediaPlayer()
        finish()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset() // Reset
                player.release()
            } catch (e: Exception) {
                Log.e("NotificationActivity", "Error releasing MediaPlayer", e)
            }
        }
        mediaPlayer = null
    }

    override fun onBackPressed() {
        stopNotification()
        super.onBackPressed()
    }

    override fun onDestroy() {
        releaseMediaPlayer()
        super.onDestroy()
    }
}
