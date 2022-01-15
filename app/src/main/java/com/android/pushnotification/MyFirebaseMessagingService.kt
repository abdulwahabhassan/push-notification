package com.android.pushnotification

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    //LocalBroadcastManager is an helper to register for and send broadcasts of Intents to
    //local objects within your app
    private var localBroadcastManager: LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()

        //create an instance of local broadcast manager
        //localBroadcastManager = LocalBroadcastManager.getInstance(this)
    }

    //Called if the FCM registration token is updated. This may occur if the security of
    //the previous token had been compromised. Note that this is called when the
    //FCM registration token is initially generated so this is where you would retrieve the token.
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token") //we could save to database

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token)
    }

    //When receiving notifications, it’s important to distinguish whether the app is running on the
    //foreground or background. Basically, you need to consider the following:
    //-Foreground: The notification and the data are both handled in onMessageReceived()
    //-Background: The System UI handles the notification.
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        //handle the message received
        handleMessage(p0)
    }


    private fun handleMessage(remoteMessage: RemoteMessage) {

        remoteMessage.data["title"]?.let { title ->
            remoteMessage.data["body"]?.let {  body ->
                NotificationBuilder(this, body, title).postNotification()
            }
        }

//        //broadcast the remote message received within your app
//        remoteMessage.notification?.let {
//            val intent = Intent("FCMPushNotification")
//            intent.putExtra("message", it.body)
//            localBroadcastManager?.sendBroadcast(intent)
//        }
    }


    companion object {
        private const val TAG = "MyFirebaseMessagingS"
    }
}

