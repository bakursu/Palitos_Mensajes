/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.otros



import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bakursu.palitosmensajes.chat.MensajesActivity
import com.bakursu.palitosmensajes.chat.MensajesActivity.Companion.UserActual
import com.bakursu.palitosmensajes.databinding.ActivityConfigBinding
import com.bakursu.palitosmensajes.extensiones.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class ConfigActivity : AppCompatActivity() {

    val TAG = "config"
    lateinit var binding: ActivityConfigBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConfigBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Actualiza tus datos"
        binding.BtnConfigFotoPerfil.alpha = 0f

        binding.BtnInfo.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }
        binding.TVConfigUser.text = UserActual?.username
        Picasso.get().load(UserActual?.perfilImagen).into(binding.IVConfigPerfil)



        binding.BtnConfigFotoPerfil.setOnClickListener { selectPhoto() }

        binding.BtnUpdate.setOnClickListener {
            val newName = binding.ETConfigUser.text.toString()
            if (!newName.isBlank()  || selectImageUri != null) {
                Log.d(TAG, "nombre $newName")
                if ( newName != UserActual?.username){
                subirImagenFirebase()
                }else{
                    Toast.makeText(this, "no hay nada que cambiar", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "no hay nada que cambiar", Toast.LENGTH_SHORT).show()
            }

        }


    }



    private fun actualizarFotoPerfil() {
        TODO("Not yet implemented")
    }

    var selectImageUri: Uri? = null
    private fun selectPhoto() {
        Log.d(TAG, "seleccionar foto")


        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/+"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //toma el intent result y selecciona la imagen
            Log.d(TAG, "foto seleccionada")
            selectImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectImageUri)

            binding.IVConfigPerfil.setImageBitmap(bitmap)
            binding.BtnConfigFotoPerfil.visibility = View.VISIBLE
            binding.BtnConfigFotoPerfil.alpha = 0f


//            val bitmapDrawable = BitmapDrawable(bitmap)
//            binding.BtnFotoPerfil.setBackgroundDrawable(bitmapDrawable)
//            binding.BtnFotoPerfil.text = null
        }
    }

    private fun subirImagenFirebase() {
        if (selectImageUri == null) return saveUserBDD(UserActual!!.perfilImagen)

        Log.d(TAG, "subirImagenFirebase: ")
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectImageUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "imagen subida sin problmeas UUid ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d(TAG, "url: $it ")
                    saveUserBDD(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "crisis la imagen fallo")
            }
    }

    private fun saveUserBDD(perfilImagen: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        var newName = binding.ETConfigUser.text.toString()
        if (newName.isBlank()) newName = UserActual!!.username
        val user = User(uid, newName , perfilImagen)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "subido a la bdd ")
                Log.d(TAG, "nuevo nombre $newName, url: $perfilImagen")
                finish()
                GotoMain()
            }
            .addOnFailureListener {
                Log.d(TAG, "fallo al guardar ${it.message}")
            }
            .addOnCanceledListener {
                Log.d(TAG, "cancelado al guardar ")
            }
    }

    private fun GotoMain() {
        startActivity(Intent(this, MensajesActivity::class.java))
        Log.d(TAG, "en camino al main")
        finish()
    }

    override fun onBackPressed() {

        super.onBackPressed()
        finish()
    }
}