package com.android.pushnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.pushnotification.databinding.ActivityNotificationBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.common.GooglePlayServicesUtilLight
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    //create a broadcast receiver that will receive the intent that will be broadcast by the instance
    //of MyFirebaseMessagingService local broadcast manager when a new notification arrives from Fire
    //base cloud and is to be handled
    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //do something with the intent if it is not null
//            intent?.let {
//                //filter the action of the intent to perform appropriate action with the right one
//                if (it.action == "FCMPushNotification") {
//                    //display the text
//                    binding.textViewNotification.text = intent.getStringExtra("message")
//                }
//
//            }
        }
    } //now register this broadcast receiver with the instance of LocalBroadcastManager in OnStart()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //get the intent that started this activities,
        //check if its bundle contains the specifiedKey
        //use the value of the key
        val bundle = intent.extras
        if (bundle != null) {
           binding.textViewNotification.text = bundle.getString(DATA_PAYLOAD_MESSAGE)
        }

        //On initial startup of your app, the FCM SDK generates a registration token for
        //the client app instance.
        //you'll need to access this token by extending FirebaseMessagingService and
        //overriding onNewToken.

        binding.buttonRetrieveToken.setOnClickListener {

            //if app has Google play services installed, go ahead and retrieve token
            if (checkGooglePlayServices()) {
                Log.w(TAG, "Device has google play services")
                //retrieve token
                getFCMRegistrationToken()

            } else {
                //You won't be able to send notifications to this device
                Log.w(TAG, "Device doesn't have google play services")

            }

        }
    }

    private fun getFCMRegistrationToken() {
        //Because the token could be rotated after initial startup, you are strongly recommended
        //to retrieve the latest updated registration token.

        //To retrieve the registration token for the client app instance
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            //Get new FCM registration token if task was successful
            val token = task.result

            //The device token is a unique identifier that contains two things:
            //- Which device will receive the notification.
            //- The app within that device that will receive the notification.

            // Retrieve token as a String, Log and toast it
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

            //When the device token is retrieved, Firebase can now connect with the device

        })

    }

    //Apps that rely on the Play Services SDK should always check the device for a compatible
    //Google Play services APK before accessing Google Play services features.
    //It is recommended to do this in two places: in the main activity's onCreate() method,
    //and in its onResume() method.

    //When sending push notifications in Android, it’s important to make sure the devices
    //you’re pushing to have Google Play Services enabled. Otherwise, they won’t be able to
    //receive your messages.

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(this)
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error")
            //Ask user to update google play services and manage the error.
            false
        } else {
            Log.i(TAG, "Google play services updated")
            true
        }

    }

    override fun onStart() {
        super.onStart()
        //register broadcaster receiver with instance of LocalBroadcastManager
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageReceiver, IntentFilter("FCMPushNotification"))
    }

    override fun onStop() {
        super.onStop()
        //it is important to unregister any broadcaster receiver in this activity when the lifecycle
        //stops
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)

    }


    companion object {
        const val DATA_PAYLOAD_TITLE = "DataPayloadMessageTitle"
        private const val TAG = "NotificationActivity"
        const val DATA_PAYLOAD_MESSAGE = "body"
    }

}

//There are several ways to set the notification’s icon:
//-Don’t assign an icon. In this case, the app icon will appear.
//-Set it in Android Manifest.
//-Send it in the notification payload.

