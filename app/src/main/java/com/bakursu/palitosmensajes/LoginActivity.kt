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


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bakursu.palitosmensajes.chat.MensajesActivity
import com.bakursu.palitosmensajes.databinding.ActivityLoginBinding
import com.bakursu.palitosmensajes.extensiones.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    val GOOGLE_LOGIN = 100
    lateinit var binding: ActivityLoginBinding
    var tag = "LoginAct"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.BtnGoRegister.setOnClickListener {
            gotoRegister()
        }
        binding.BtnLogin.setOnClickListener {
//            it.hideKeyboard()
            login()
        }
        binding.BtnLoginGoogle.setOnClickListener {
//            it.hideKeyboard()
            loginGoogle()
        }

    }

    private fun loginGoogle() {
        val googleconf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        val googleClient = GoogleSignIn.getClient(this, googleconf)

        startActivityForResult(googleClient.signInIntent, 100)
    //registerForActivityResult()

    }

    private fun login() {
        val correo = binding.ETCorreo.text.toString()
        val pass = binding.ETPassword.text.toString()

        Log.d(tag, "login correo $correo")

        if (validar()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener {
                    gotoMensajes()
                }
        }

    }

    private fun validar(): Boolean {
        if (binding.ETCorreo.text.toString().isBlank() || binding.ETPassword.text.toString()
                .isBlank()
        ) {
            Toast.makeText(this, "complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun gotoMensajes() {
        val intent = Intent(this, MensajesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun gotoRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

   /* fun View.hideKeyboard() {
//        val inm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inm.hideSoftInputFromWindow(windowToken, 0)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {


                val cuenta = task.getResult(ApiException::class.java)

                if (cuenta != null) {
                    val credencial = GoogleAuthProvider.getCredential(cuenta.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {


                                saveBDD(
                                    it.result.user?.displayName ?: "",
                                    it.result.user?.uid ?: "",
                                    it.result.user?.photoUrl
                                )
                                Log.d(tag, "login por google")

                            } else {
                                Toast.makeText(this, "fallo en el ingreso", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d(tag, "error al ingresar google")
                            }


                        }


                }
            } catch (e: ApiException) {
                Log.d(tag, "fallo en api $e")
            }


        }
    }

    private fun saveBDD(usuario: String, googleUid: String, perfilImagen: Uri?) {

        val perfil = perfilImagen
        val perfilstring = perfil.toString()
        val uid = googleUid
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user = User(uid, usuario, perfilstring)
        var test = "salio? "


        ref.child("user").get().addOnSuccessListener {
            test = it.value.toString()
            Log.d(tag, "funciono")
            gotoMensajes()
        }.addOnFailureListener {
            test = "no existe"
            Log.d(tag, "guardar")
            ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(tag, "subido a la bdd")
                    gotoMensajes()
                }
                .addOnFailureListener {
                    Log.d(tag, "fallo al guardar ${it.message}")
                }
                .addOnCanceledListener {
                    Log.d(tag, "cancelado al guardar ")
                }
            gotoMensajes()
        }


        Log.d(tag, "comparando $test")


    }
}
