/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:34
 */

package com.bakursu.palitosmensajes.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.bakursu.palitosmensajes.R
import com.bakursu.palitosmensajes.extensiones.ChatMensaje
import com.bakursu.palitosmensajes.extensiones.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.rv_chat_from.view.*
import kotlinx.android.synthetic.main.rv_chat_to.view.*
import java.sql.Date
import java.text.SimpleDateFormat




class ChatLogActivity : AppCompatActivity() {
    companion object {
        const val TAG = "chatlog"
        var user: User? = null
    }

    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        user = intent.getParcelableExtra(NewMensajes.USER_KEY)

        supportActionBar?.title = user?.username
        supportActionBar


        heyListen()
        RV_ChatLog.adapter = adapter


        Btn_enviar.setOnClickListener {
            Log.d(TAG, "Enviando Mensaje. . .")
            enviadoMsn()
        }



        ET_SendMensaje.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.i(TAG, "IME_ACTION_DONE")
                enviadoMsn()
                return@OnEditorActionListener true
            }
            false
        })
//        este metodo no me permite mantener el telcado

//        ET_SendMensaje.setOnKeyListener { v, keyCode, event ->
//            showSoftKeyboard(v)
//            when {
//                //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
//                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
//                    //perform an action here e.g. a send message button click
//
//                    enviadoMsn()
//
//                    //return true
//                    return@setOnKeyListener true
//                }
//                else -> false
//            }
//        }
    }

    private fun heyListen() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/u-mensajes/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMensaje = snapshot.getValue(ChatMensaje::class.java)
                if (chatMensaje != null) {
                    val horas = SimpleDateFormat("hh:mm")
                    val date = Date(chatMensaje.timestamp)
                    val time = horas.format(date)
                    Log.d(TAG, chatMensaje.text)

                    if (chatMensaje.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMensaje.text, time))
                    } else {
                        adapter.add(ChatToItem(chatMensaje.text, time))
                    }
                }

                RV_ChatLog.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun enviadoMsn() {
        //enviando por Firebase
        val user = intent.getParcelableExtra<User>(NewMensajes.USER_KEY)
//        val ref = FirebaseDatabase.getInstance().getReference("/mensajes").push()

        val text = ET_SendMensaje.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        if (fromId == null || toId == null) return
        val time = System.currentTimeMillis()
        val ref = FirebaseDatabase.getInstance().getReference("/u-mensajes/$fromId/$toId").push()
        val backref =
            FirebaseDatabase.getInstance().getReference("/u-mensajes/$toId/$fromId").push()
        val chatMensajes =
            ChatMensaje(ref.key!!, text, fromId, toId, time)
        ref.setValue(chatMensajes)
            .addOnSuccessListener {
                Log.d(TAG, "mensaje guardado ${ref.key}")
                ET_SendMensaje.text.clear()
                RV_ChatLog.scrollToPosition(adapter.itemCount - 1)
            }
        backref.setValue(chatMensajes)

        val ultimoMensaje =
            FirebaseDatabase.getInstance().getReference("/Last-mensaje/$fromId/$toId")
        val toultimoMensaje =
            FirebaseDatabase.getInstance().getReference("/Last-mensaje/$toId/$fromId")
        ultimoMensaje.setValue(chatMensajes)
        toultimoMensaje.setValue(chatMensajes)

    }

    fun showSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)

    }
}

class ChatFromItem(val text: String, private val hora: String?) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int = R.layout.rv_chat_from

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.TV_mensajeFrom.text = text
        viewHolder.itemView.TV_horaFrom.text = hora
    }

}

class ChatToItem(val text: String, private val hora: String?) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int = R.layout.rv_chat_to

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.TV_mensajeTo.text = text
        viewHolder.itemView.TV_horaTo.text = hora
    }

}