/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.chat



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bakursu.palitosmensajes.R
import com.bakursu.palitosmensajes.databinding.ActivityNewMensajesBinding
import com.bakursu.palitosmensajes.extensiones.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.rv_new_mensaje.view.*

class NewMensajes : AppCompatActivity() {
    lateinit var binding: ActivityNewMensajesBinding
    val log = "NewMensajes"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMensajesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Nuevo Chat"

//        val adapter = GroupAdapter<GroupieViewHolder>()
//     adapter.add(UserItem())
//        binding.RVNewMensaje.adapter = adapter


             getUser()
    }


    companion object{
        val  USER_KEY="USER_KEY"
        val  PHOTO_KEY="PHOTO_KEY"
    }
    private fun getUser() {
        val ref = FirebaseDatabase.getInstance().getReference("/user")

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach{
                    Log.d(log,it.toString())
                    val user = it.getValue(User::class.java)
                    if (user !=null)adapter.add(UserItem(user))

                }

                adapter.setOnItemClickListener{item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    intent.putExtra(PHOTO_KEY, item.user.perfilImagen)
                    startActivity(intent)
                    finish()

                }
                binding.RVNewMensaje.adapter = adapter


            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(log,"error $error")
            }
        })

    }
}
class UserItem(val user:User):Item<GroupieViewHolder>(){
    override fun getLayout(): Int  = R.layout.rv_new_mensaje
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.rv_UserName.text = user.username
        Picasso.get().load(user.perfilImagen).into(viewHolder.itemView.IV_NewMensaje)
    }

}