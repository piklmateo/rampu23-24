package hr.foi.rampu.eventmanager.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hr.foi.rampu.eventmanager.R
import java.util.Date

class EventNotificationReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "qr_notification_channel"
    private val NOTIFICATION_ID = 1

    @SuppressLint("SimpleDateFormat")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {

            val eventDateStr = intent.getStringExtra("EVENT_DATE")
            val eventName = intent.getStringExtra("EVENT_NAME")
            val eventDate = SimpleDateFormat("dd.MM HH:mm").parse(eventDateStr)

            showNotification(context, eventDate, eventName)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context?, eventDate: Date?, eventName: String?) {
        if (context != null && eventDate != null && eventName != null) {
            val formattedTime = SimpleDateFormat("dd.MM HH:mm").format(eventDate)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("24h dijeli vas do eventa $eventName")
                .setContentText("Točno vrijeme i datum: $formattedTime")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}
