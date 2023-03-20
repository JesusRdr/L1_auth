package com.example.ejercicio1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// import kotlinx.android.synthetic.main.activity_pantalla_mail.*

class pantallaMail : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_mail)
        // Constructor para cambiar de pantalla "BACK"
        val btn_1: Button = findViewById(R.id.btnBack)
        btn_1.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
            // Setup
            setup()
    }
        // Constructor para hacer signUP
        private fun setup() {
            title = "Email & Password - Verification Link"
           val btn2: Button = findViewById(R.id.btnUp)
            btn2.setOnClickListener {
           val edt: EditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
           val edt2: EditText = findViewById<EditText>(R.id.editTextTextPassword)
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty()){
                  // DOCUMENTACION -->  auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edt.text.toString(),edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        //showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                        verifyEmail(user)
                    }else{
                        showAlert()

                    }
                }
            }
        }

        // Constructor para hacer Login
        val btn3: Button = findViewById(R.id.btnIn)
        btn3.setOnClickListener {
            val edt: EditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
            val edt2: EditText = findViewById<EditText>(R.id.editTextTextPassword)
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.text.toString(),edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }else{
                        showAlert()

                    }
                }
            }
        }
    }

    private fun showAlert() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Error")
    builder.setMessage("Se ha producido un error autenticando al usuario")
    builder.setPositiveButton("Aceptar", null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}
    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, Logged_screen::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
                    task->
                if(task.isComplete){
                    Toast.makeText(this,"Email has send", Toast.LENGTH_LONG).show()
                    //showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                }
                else{
                    Toast.makeText(this,"An error has occurred while sending email", Toast.LENGTH_LONG).show()
                }
            }
    }

}
