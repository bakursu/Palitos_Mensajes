package com.bakursu.palitosmensajes
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.bakursu.palitosmensajes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val tag = "LoginAct"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.BtnRegister.setOnClickListener {
            it.hideKeyboard()
            btnRegister()

        }
        binding.BtnGoLogin.setOnClickListener {
            it.hideKeyboard()
            GotoLogin()
        }
    }

    private fun GotoLogin() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun btnRegister() {
        val correo = binding.ETCorreo.text.toString()
        val user = binding.ETUsuario.text.toString()
        val pass = binding.ETPassword.text.toString()
        val confirmPass = binding.ETConfirmPass.text.toString()
        Log.d(tag, "user is: $user")
        Log.d(tag, "correo es $correo")
    }


    fun View.hideKeyboard() {
        val inm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(windowToken, 0)
    }
}