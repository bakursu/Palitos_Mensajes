/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.extensiones



import android.util.Log
import com.bakursu.palitosmensajes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.rv_last_mensajes.view.*


class LastMensajes(val chat: ChatMensaje): Item<GroupieViewHolder>(){
    var chatPartnerUser: User? = null

    override fun getLayout(): Int = R.layout.rv_last_mensajes
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartner:String
        if(chat.fromId == FirebaseAuth.getInstance().uid){
            chatPartner = chat.toId
        }else  {
            chatPartner = chat.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/user/$chatPartner")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.TV_lastUsername.text =  chatPartnerUser?.username
                val perfil = viewHolder.itemView.IV_lastPerfil
                Picasso.get().load(chatPartnerUser?.perfilImagen).into(perfil)
            }


            override fun onCancelled(error: DatabaseError) {
                Log.d("MensajesAct", "error $error")
            }
        })

        viewHolder.itemView.TV_lastMensaje.text = chat.text

    }
}