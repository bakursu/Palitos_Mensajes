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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.bakursu.palitosmensajes.databinding.ActivityRegisterBinding
import com.bakursu.palitosmensajes.extensiones.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    val tag = "LoginAct"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        binding.BtnFotoPerfil.setOnClickListener {
            btnSelectPhoto()
        }
        binding.BtnRegister.setOnClickListener {
        //   it.hideKeyboard()
            btnRegister()

        }
        binding.BtnGoLogin.setOnClickListener {
//            it.hideKeyboard()
            GotoLogin()
        }
    }

    private fun btnSelectPhoto() {
        Log.d(tag, "seleccionar foto")


        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/+"
        startActivityForResult(intent, 0)
    }

    private fun GotoLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    var selectImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //toma el intent result y selecciona la imagen
            Log.d(tag, "foto seleccionada")
            selectImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectImageUri)

            binding.IVPerfil.setImageBitmap(bitmap)
            binding.BtnFotoPerfil.alpha = 0f


//            val bitmapDrawable = BitmapDrawable(bitmap)
//            binding.BtnFotoPerfil.setBackgroundDrawable(bitmapDrawable)
//            binding.BtnFotoPerfil.text = null
        }
    }

    private fun btnRegister() {
        val correo = binding.ETCorreo.text.toString()
        val pass = binding.ETPassword.text.toString()
        // ahora vamos con firebase
        if (valdatos()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener {
                    Log.d(tag,"complete")

                        if (it.isSuccessful) {
                        //else if success
                        Log.d(tag, "btnRegister: user con id ${it.result.user?.uid}")
                        subirImagenFirebase()
                        //GotoLogin()
                        return@addOnCompleteListener
                    }

                }
                .addOnFailureListener {
                    Log.d(tag, "fallo al crear el usuario ${it.message}")
                    Toast.makeText(this, "error al crear usuario", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun subirImagenFirebase() {
        val furi= "https://firebasestorage.googleapis.com/v0/b/palitos-mensajes.appspot.com/o/images%2Fstickman.png?alt=media&token=f0ae9f19-1f9b-4893-835f-349ca12f90fb"
        if (selectImageUri == null) return saveUserBDD(furi)

        Log.d(tag, "subirImagenFirebase: ")
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectImageUri!!)
            .addOnSuccessListener {
                Log.d(tag, "imagen subida sin problmeas UUid ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d(tag, "url: $it ")
                    saveUserBDD(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(tag, "crisis la imagen fallo")
            }
    }

    private fun saveUserBDD(perfilImagen: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user = User(uid, binding.ETUsuario.text.toString(), perfilImagen)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(tag, "subido a la bdd")
                GotoLogin()

            }
            .addOnFailureListener {
                Log.d(tag, "fallo al guardar ${it.message}")
            }
            .addOnCanceledListener {
                Log.d(tag, "cancelado al guardar ")
            }
    }

    private fun valdatos(): Boolean {
        if (binding.ETUsuario.text.toString().isBlank() || binding.ETCorreo.text.toString()
                .isBlank() || binding.ETPassword.text.toString().isBlank()
        ) {
            Toast.makeText(this, "por favor rellene todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(binding.ETCorreo.text).matches()) {
            binding.ETCorreo.error = "ingrese un correo valido"
            return false
        }
        if (binding.ETPassword.text.toString().length <= 7) {
            binding.ETPassword.error = "debe ser mayor a 8 caracteres"
            return false
        }
        if (binding.ETPassword.text.toString() != binding.ETConfirmPass.text.toString()) {
            binding.ETConfirmPass.error = "la contraseña debe coincidir con la anterior"
            return false
        }
        if (selectImageUri == null) {
            val ref = FirebaseStorage.getInstance().getReference("/images/stickman.png")
            ref.downloadUrl.addOnSuccessListener{
                selectImageUri = it
                Log.d(tag,"imagen default")
            }
           // https://firebasestorage.googleapis.com/v0/b/palitos-mensajes.appspot.com/o/images%2Fa5cd0431-82c6-4571-9e32-971fabe82061?alt=media&token=eccfd170-e896-4634-9f62-2c1df711f953

        }

        return true
    }


   /* fun View.hideKeyboard() {
//        val inm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inm.hideSoftInputFromWindow(windowToken, 0)
    }*/
}


