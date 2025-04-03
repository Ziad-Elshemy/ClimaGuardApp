package eg.iti.mad.climaguard.worker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.newsapp.api.ApiManager
import eg.iti.mad.climaguard.api.WeatherRemoteDataSourceImpl
import eg.iti.mad.climaguard.local.LocationsLocalDataSourceImpl
import eg.iti.mad.climaguard.local.MyDatabase
import eg.iti.mad.climaguard.repo.Repository
import eg.iti.mad.climaguard.repo.RepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val dateTime = intent.getLongExtra("DATE_TIME", -1L)
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        val repo: Repository = RepositoryImpl.getInstance(
            WeatherRemoteDataSourceImpl(ApiManager.getApis()),
            LocationsLocalDataSourceImpl(
                MyDatabase.getInstance(context).locationDao(),
                MyDatabase.getInstance(context).alarmDao()
            )
        )


        if (dateTime != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                repo.removeAlarmById(dateTime)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)

            Log.d("DismissReceiver", "Dismiss action triggered, item deleted from database.")
        }
    }
}