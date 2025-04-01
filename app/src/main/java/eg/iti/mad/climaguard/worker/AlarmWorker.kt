package eg.iti.mad.climaguard.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import eg.iti.mad.climaguard.R

class AlarmWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("AlarmWorker", "doWork() started")
        val type = inputData.getString("TYPE") ?: "Notification"
        val title = inputData.getString("TITLE") ?: "Alarm"
        val message = inputData.getString("MESSAGE") ?: "It's time!"

        Log.d("AlarmWorker", "Type: $type, Title: $title, Message: $message")

        if (type == "Notification") {
            showNotification(applicationContext, title, message)
        } else {
            playAlarm(applicationContext)
        }
        return Result.success()
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "alarm_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarms", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.clock)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun playAlarm(context: Context) {
        Log.d("AlarmWorker", "Playing alarm sound")
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound)
        mediaPlayer.start()
    }
}
