package com.example.lostnfound.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.lostnfound.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId="Notification_channel"
const val channelName="com.example.lostnfound.activities"
class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
     if(remoteMessage.notification !=null)
     {
         generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!);
     }
    }


    fun getRemoteView(title: String,message: String): RemoteViews {
        val remoteView=RemoteViews("com.example.lostnfound",R.layout.notification)
    remoteView.setTextViewText(R.id.title,title)
    remoteView.setTextViewText(R.id.message,message)
    remoteView.setImageViewResource(R.id.icon,R.drawable.indian_institute)
    return remoteView
    }


    fun generateNotification(title:String,message:String) {
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.indian_institute)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)

        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
             val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
             notificationManager.createNotificationChannel(notificationChannel)
         }
      notificationManager.notify(0,builder.build())

    }
}