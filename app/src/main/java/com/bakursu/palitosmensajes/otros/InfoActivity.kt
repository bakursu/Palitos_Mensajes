/*
 * realizado por palitodev Jose Rios
 * correo jdrios.c7@gmail.com
 *  21/09/22 12:37
 */

package com.bakursu.palitosmensajes.otros



import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bakursu.palitosmensajes.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Creditos"

        val gmail1 = binding.IVGmail
        val gmail2 = binding.TVGmail
        val instagram1 = binding.IBtnInstagram
        val instagram2 = binding.TVInstagram

        gmail1.setOnClickListener { gmail() }
        gmail2.setOnClickListener { gmail() }

        instagram1.setOnClickListener { instagram() }
        instagram2.setOnClickListener { instagram() }
    }

    private fun instagram() {
        val uri = Uri.parse("https://www.instagram.com/palitodev/")
        val intent = Intent( Intent.ACTION_VIEW,uri)
        startActivity(intent)
    }

    private fun gmail() {
        val mail = "jdrios.c7@gmail.com"
        val clipboar = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("simple", mail)
        
        clipboar.setPrimaryClip(clip)
        Toast.makeText(this, "Correo copiado al portapapeles", Toast.LENGTH_SHORT).show()
        val uri = Uri.parse("mailto:$mail")
        val intent = Intent( Intent.ACTION_VIEW,uri)
        Thread.sleep(1500)
        startActivity(intent)
    }
}