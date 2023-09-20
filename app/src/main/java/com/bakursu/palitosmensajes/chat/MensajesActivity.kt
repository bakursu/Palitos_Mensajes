/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.chat



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.toColor
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import com.bakursu.palitosmensajes.LoginActivity
import com.bakursu.palitosmensajes.R
import com.bakursu.palitosmensajes.chat.NewMensajes.Companion.USER_KEY
import com.bakursu.palitosmensajes.databinding.ActivityMensajesBinding
import com.bakursu.palitosmensajes.extensiones.ChatMensaje
import com.bakursu.palitosmensajes.extensiones.LastMensajes
import com.bakursu.palitosmensajes.extensiones.User
import com.bakursu.palitosmensajes.otros.ConfigActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_mensajes.*

class MensajesActivity : AppCompatActivity() {
    companion object{
        var UserActual: User? =null
    }

    private lateinit var binding: ActivityMensajesBinding
    val TAG = "mensajesAct"
    val adapter = GroupAdapter<GroupieViewHolder>()
    val lastMap = HashMap<String,ChatMensaje>()

    var saludosUser = "Saludos"

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.title = saludosUser
        super.onCreate(savedInstanceState)
        binding = ActivityMensajesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) getCurrentUser(uid)
        verificarLogin()


        ultimosMensajes()
        setupAdapter()
        RV_LastMensajes.adapter = adapter
        RV_LastMensajes.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        setup()
    }

    private fun setup() {

        binding.fabMensajes.setOnClickListener {
            val intent = Intent(this, NewMensajes::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapter() {
        adapter.setOnItemClickListener{ item, _ ->
            val lastMensajes = item as LastMensajes
            val intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, lastMensajes.chatPartnerUser)
            startActivity(intent)

        }
    }

    private fun refreshRV(){
        adapter.clear()
        lastMap.values.forEach{
            adapter.add(LastMensajes(it))
        }
    }

    private fun ultimosMensajes() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Last-mensaje/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMensaje = snapshot.getValue(ChatMensaje::class.java) ?:return
                lastMap[snapshot.key!!] = chatMensaje
                refreshRV()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMensaje = snapshot.getValue(ChatMensaje::class.java) ?:return
                lastMap[snapshot.key!!] = chatMensaje
                refreshRV()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }




    private fun getCurrentUser(uid:String) {
        //conseguir usarios de realtime database
        //verifico la ruta del usuario
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        //genero un listener
        ref.addListenerForSingleValueEvent(object: ValueEventListener{ //opero con el objeto que me devolvera
            override fun onDataChange(snapshot: DataSnapshot) {
                //en mi caso tengo un json (el valor USER::class.java es mi json) que me ayuda a recibir los datos
                UserActual = snapshot.getValue(User::class.java)
                //con la variable UserActual (termina funcionando como un diccionario) manipulo los datos que necesito
                Log.d(TAG,"a ingresado ${UserActual?.username}")

                supportActionBar?.title = "Saludos ${UserActual?.username}"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "error $error ")
            }
        })
    }

    private fun verificarLogin() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

// toolbar configuracion
    override fun onOptionsItemSelected(item: MenuItem): Boolean {



        when(item.itemId){
            R.id.menu_NewMensaje ->{
                val intent = Intent(this, ConfigActivity::class.java)

                startActivity(intent)
            }
            R.id.menu_Sign_Out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}