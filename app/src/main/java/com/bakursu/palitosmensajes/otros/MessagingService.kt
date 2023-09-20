/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.otros
/**
* realizado por palitodev
* Jose Rios
* correo jdrios.c7@gmail.com
* creado el 11:26 a. m.  21/09/2022
**/


import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService:FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Looper.prepare()
        Handler().post{
            Toast.makeText(baseContext, "${message.notification?.title} dice ${message.notification?.body}", Toast.LENGTH_SHORT).show()
        }
        Looper.loop()
    }
}