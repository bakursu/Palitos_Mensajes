/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes
/**
* realizado por palitodev
* Jose Rios
* correo jdrios.c7@gmail.com
* creado el 11:26 a. m.  21/09/2022
**/


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bakursu.palitosmensajes.chat.MensajesActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        val splah = installSplashScreen()
        super.onCreate(savedInstanceState)
        splah.setKeepOnScreenCondition { false }


        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d("splash","en camino al login")
            finish()
        } else {
            startActivity(Intent(this, MensajesActivity::class.java))
            Log.d("splash","en camino al main")
            finish()
        }
    }

}