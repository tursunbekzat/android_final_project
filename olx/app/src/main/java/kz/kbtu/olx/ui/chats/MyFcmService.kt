package kz.kbtu.olx.ui.chats

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kz.kbtu.olx.R
import kotlin.random.Random

class MyFcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = "${remoteMessage.notification?.title}"
        val body = "${remoteMessage.notification?.body}"

        val senderUid = "${remoteMessage.data["senderUid"]}"
        val notificationType = "${remoteMessage.data["notificationType"]}"

        showChatsNotification(title, body, senderUid)
    }


    private fun showChatsNotification(title: String, body: String, senderUid: String) {

        val notificationId = Random.nextInt(3000)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        setUpNotificationChannel(notificationManager)

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("receiptUid", senderUid)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    private fun setUpNotificationChannel(notificationManager: NotificationManager){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(

                NOTIFICATION_CHANNEL_ID,
                "Chat Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.description = "Show Chat Notifications"
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private companion object{

        private const val NOTIFICATION_CHANNEL_ID = "OLX_CHANNEL_ID"
    }

}
