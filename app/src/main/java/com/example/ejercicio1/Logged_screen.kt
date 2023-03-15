package com.example.ejercicio1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC
}

class Logged_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_screen)
        // SETUP
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
       setup(email ?: "", provider ?: "")
    }
    private fun setup(email: String, provider: String) {
        title = "WELCOME"

        val edt3: TextView = findViewById<EditText>(R.id.emailTextView)
        val edt4: TextView = findViewById<EditText>(R.id.providerTextView)
        edt3.text = email
        edt4.text = provider

        val btn4: Button = findViewById(R.id.btnOut)
        btn4.setOnClickListener {
        FirebaseAuth.getInstance().signOut()
                val intent: Intent = Intent(this, MainActivity:: class.java)
                startActivity(intent)
            }
        }
        }