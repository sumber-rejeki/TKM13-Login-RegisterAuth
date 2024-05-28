package com.example.firebase_auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase_auth.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //auth firebase
        auth = FirebaseAuth.getInstance()

        //Email validation
        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe{
            showEmailValidAlert(it)
        }

        //Reset password
        binding.btnResetPw.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { reset->
                    if(reset.isSuccessful){
                        Intent(this, LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            Toast.makeText(this, "Periksa email untuk mengatur ulang password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, reset.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //Click
        binding.tvBackLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun showEmailValidAlert(isNotValid: Boolean){
        if(isNotValid){
            binding.etEmail.error = "Email tidak valid!"
            binding.btnResetPw.isEnabled = false
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        } else {
            binding.etEmail.error = null
            binding.btnResetPw.isEnabled = true
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
        }
    }
}