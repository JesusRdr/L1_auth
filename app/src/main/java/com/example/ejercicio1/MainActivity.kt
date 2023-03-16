package com.example.ejercicio1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.btn_mail)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, pantallaMail:: class.java)
            startActivity(intent)
            }
        session()
        setup()
        }




    private fun setup() {
        val btnG: Button = findViewById(R.id.btnGoogle)
        btnG.setOnClickListener {
            // Configuracion para boton de GOOGLE
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
        val btnPhoneval: Button = findViewById(R.id.btn_phone)
        btnPhoneval.setOnClickListener {
            // Configuracion para boton de Phone
            val intent: Intent = Intent(this, pantalla_phone:: class.java)
            startActivity(intent)
        }


    }

    private fun session(){
    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
    val email = prefs.getString("email", null)
    val provider = prefs.getString("provider", null)

    if(email != null && provider != null){
        showHome(email, ProviderType.valueOf(provider))
    }
}

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se haproducido un error autenticando al usuario")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(account.email ?: "", ProviderType.GOOGLE)
                        }else{
                            showAlert()

                        }
                    }
                }
            } catch (e: ApiException){
                showAlert()
            }

        }
    }
}
