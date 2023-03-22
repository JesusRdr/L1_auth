package com.example.ejercicio1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

// import kotlinx.android.synthetic.main.activity_pantalla_mail.*

class pantallaMail : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_mail)
        // Constructor para cambiar de pantalla "BACK"
        //val btn_10: Button =findViewById(R.id.btnIn)
        //btn_10.visibility = View.INVISIBLE

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

                        /*for (i in 1..5) {
                            waiting()
                            println(user?.isEmailVerified)
                            if (user != null) {
                                println(user.isEmailVerified)
                                if (user.isEmailVerified) {
                                    val btn_12: Button =findViewById(R.id.btnIn)
                                    btn_12.visibility = View.VISIBLE
                                } } }*/

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
                //checkIfEmailVerified(user)

                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.text.toString(),edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
             /*
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.toString(),
                    edt2.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                            // User is not enrolled with a second factor and is successfully
                            // signed in.
                            // ...
                            return@addOnCompleteListener
                        }
                        if (task.exception is FirebaseAuthMultiFactorException) {
                            val multiFactorResolver =(task.exception as FirebaseAuthMultiFactorException).resolver
                            // Ask user which second factor to use. Then, get
                            // the selected hint:
                            val selectedHint =
                                multiFactorResolver.hints[selectedIndex] as PhoneMultiFactorInfo
                            // Send the SMS verification code.
                            PhoneAuthProvider.verifyPhoneNumber(
                                PhoneAuthOptions.newBuilder()
                                    .setActivity(this)
                                    .setMultiFactorSession(multiFactorResolver.session)
                                    .setMultiFactorHint(selectedHint)
                                    .setCallbacks(generateCallbacks())
                                    .setTimeout(30L, TimeUnit.SECONDS)
                                    .build()
                            )

                            // Ask user for the SMS verification code, then use it to get
                            // a PhoneAuthCredential:
                            val credential =
                                PhoneAuthProvider.getCredential(verificationId, verificationCode)

                            // Initialize a MultiFactorAssertion object with the
                            // PhoneAuthCredential.
                            val multiFactorAssertion: MultiFactorAssertion =
                                PhoneMultiFactorGenerator.getAssertion(credential)

                            // Complete sign-in.
                            multiFactorResolver
                                .resolveSignIn(multiFactorAssertion)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // User successfully signed in with the
                                        // second factor phone number.
                                    }
                                    // ...
                                }
                        } else {
                            // Handle other errors such as wrong password.
                        }
                    */
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
    fun waiting() {
        println("Stopping…")
        try {
            // sleep for one second
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        println("Resuming…")
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
