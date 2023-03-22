package com.example.ejercicio1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

enum class ProviderType{
    BASIC,
    GOOGLE,
    PRO
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

        if(provider == ProviderType.BASIC.name){
            //LinearNames.visibility = android.view.View.VISIBLE
            val user = FirebaseAuth.getInstance().currentUser
            //user?.let {
                //val name = user.displayName
                //txtNameFromDB.text = name.toString()
            //}
            //  checkIfEmailVerified(user)
        }
        //GUARDAR DATOS DE USUARIO AL CAMBIAR DE PANTALLAS
        // GESTION DE SESIONES
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup(email: String, provider: String) {
        title = "WELCOME"

        val edt3: TextView = findViewById<EditText>(R.id.emailTextView)
        val edt4: TextView = findViewById<EditText>(R.id.providerTextView)
        edt3.text = email
        edt4.text = provider

        val btn4: Button = findViewById(R.id.btnOut)
        btn4.setOnClickListener {

            // BORRADO DE DATOS EN EL FICHERO AL MOMENTO DE CERRAR SESION
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
        FirebaseAuth.getInstance().signOut()
                val intent: Intent = Intent(this, MainActivity:: class.java)
                startActivity(intent)
            }
        }

    private fun checkIfEmailVerified(user : FirebaseUser?){
        if (user != null) {
            if (user.isEmailVerified) {
                Toast.makeText(this,"Verified account, thanks!", Toast.LENGTH_LONG).show()
            }  else {
                //FirebaseAuth.getInstance().signOut() //restart this activity
                //startActivity(Intent(this,Logged_screen::class.java))
                Toast.makeText(this,"The user is not verified", Toast.LENGTH_LONG).show()

                // val intent: Intent = Intent(this, MainActivity:: class.java)
                // startActivity(intent)

            }
        }
    }
}