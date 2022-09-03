package com.bakursu.palitosmensajes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bakursu.palitosmensajes.databinding.ActivityLoginBinding
import com.bakursu.palitosmensajes.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var tag = "LoginAct"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnGoRegister.setOnClickListener{
            gotoRegister()
        }
        binding.BtnLogin.setOnClickListener{
            login()
        }

    }

    private fun login() {
        val correo = binding.ETCorreo.text.toString()
        val pass = binding.ETPassword.text.toString()

        Log.d(tag, "login correo $correo")
    }

    private fun gotoRegister() {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
    }
}