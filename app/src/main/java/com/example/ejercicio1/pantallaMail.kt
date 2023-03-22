package com.example.ejercicio1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.PhoneMultiFactorGenerator
import java.util.Objects

// import kotlinx.android.synthetic.main.activity_pantalla_mail.*
private var control: Number = 2

class pantallaMail : AppCompatActivity() {

    private lateinit var RESENDTOKEN: PhoneAuthProvider.ForceResendingToken
    private lateinit var OTP: String

    private lateinit var number: String
    private lateinit var auth: FirebaseAuth

    //private lateinit var verificationId: String
    //private lateinit var verificationCode: PhoneAuthProvider.ForceResendingToken

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_mail)

//INICIALIZANDO VARIABLES

        if (control == 1){ // SE EJECUTA SOLO LA PRIMERA VEZ

                val edit_CODE: EditText = findViewById(R.id.editTextCODE)
                edit_CODE.visibility = View.INVISIBLE
                val verification_val: Button = findViewById(R.id.btn_Verify)
                verification_val.visibility = View.INVISIBLE
    }

        auth = FirebaseAuth.getInstance()

        val btn_1: Button = findViewById(R.id.btnBack)
        btn_1.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Setup
        setup()
    }

    // Constructor para hacer signUP
    @SuppressLint("SuspiciousIndentation")
    private fun setup() {
        title = "Email & Password - Verification Link"
        val btn2: Button = findViewById(R.id.btnUp)
        btn2.setOnClickListener {
            val edt: EditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
            val edt2: EditText = findViewById<EditText>(R.id.editTextTextPassword)
            val edt3: EditText = findViewById<EditText>(R.id.editPhoneMFA)
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty() && edt3.text.isNotEmpty()) {
                // DOCUMENTACION -->  auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edt.text.toString(), edt2.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            verifyEmail(user)

                            // REGISTRO PARA MFA
                            val phone_editText2: EditText = findViewById(R.id.editPhoneMFA)
                            number = phone_editText2.text.trim().toString()


                            //val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
                            user?.multiFactor?.session?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    number = "+52$number"
                                    val multiFactorSession = task.result
                                    val phoneAuthOptions = PhoneAuthOptions.newBuilder(auth)
                                        .setPhoneNumber(number)
                                        .setTimeout(60L, TimeUnit.SECONDS)
                                        .setActivity(this)
                                        .setMultiFactorSession(multiFactorSession)
                                        .setCallbacks(callbacks)
                                        .build()
                                    // Send SMS verification code.
                                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)

                                    /*
                                    println(OTP)
                                    println(resendToken)
                                    */
                                }
                            }
                            val edit_CODE: EditText = findViewById(R.id.editTextCODE)
                            edit_CODE.visibility = View.VISIBLE
                            val verification_val: Button = findViewById(R.id.btn_Verify)
                            verification_val.visibility = View.VISIBLE

                            // waiting()

                            control = 2
                            // Ask user for the verification code.
                            // WILL CONTINUE
/*
                                    val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
                                    val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)

                                    println("entró al enroll")
                                    // Complete enrollment.
                                    FirebaseAuth.getInstance()
                                        .currentUser
                                        ?.multiFactor
                                        ?.enroll(multiFactorAssertion, "My personal phone number")
                                    // ?.addOnCompleteListener
                                    println("completó el enroll despues del listener")
        */
                                    // TERMINA EL ENROLLMENT

                        } else {
                            showAlert()
                        }
                    }
            }
        }

        val btn15: Button = findViewById(R.id.btn_Verify)
        btn15.setOnClickListener {
            println("si paso el boton de verify")

            OTP = intent.getStringExtra("OTP").toString()

            val edit_verification_val: EditText = findViewById(R.id.editTextCODE)
            val OTP_CODE_typed = edit_verification_val.text.toString()

            if (OTP_CODE_typed.isNotEmpty()) {
                println("si entró al primer IF (NOT EMPTY)")
                if (OTP_CODE_typed.length == 6){
                    println("entró al segundo IF")
                    val credential = PhoneAuthProvider.getCredential(OTP, OTP_CODE_typed)
                    val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
                    println("entró al enroll")
                    // Complete enrollment.
                    FirebaseAuth.getInstance()
                        .currentUser
                        ?.multiFactor
                        ?.enroll(multiFactorAssertion, "My personal phone number")
                        ?.addOnCompleteListener{

                        }
                    println("completó el enroll despues del listener")

                }else {
                    Toast.makeText(this,"Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }


        // Constructor para hacer Login. YA DEBE ESTAR LLENO EL NUMERO DE TELEFONO PARA HACER EL LOGIN

        val btn3: Button = findViewById(R.id.btnIn)
        btn3.setOnClickListener {
            val edt: EditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
            val edt2: EditText = findViewById<EditText>(R.id.editTextTextPassword)
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty()) {
                //checkIfEmailVerified(user)
                /*
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.text.toString(),edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        checkIfEmailVerified(user)
                        waiting()
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)

                    }else{
                        showAlert()
                    }
                }
 */

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(edt.text.toString(), edt2.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User is not enrolled with a second factor and is successfully
                            // signed in.
                            val user = FirebaseAuth.getInstance().currentUser
                            checkIfEmailVerified(user)
                            //     return@addOnCompleteListener

                            //task.exception = FirebaseAuthMultiFactorException
                            // Cómo le digo al código, que para este usuario sí quiero activar la MFA?

                            val edit_CODE2: EditText = findViewById(R.id.editTextCODE)
                            edit_CODE2.visibility = View.VISIBLE
                            val verification_val2: Button = findViewById(R.id.btn_Verify)
                            verification_val2.visibility = View.VISIBLE
                        }
                        if (task.exception is FirebaseAuthMultiFactorException) {
                            println(task.exception)
                            val multiFactorResolver = (task.exception as FirebaseAuthMultiFactorException).resolver
                            // Ask user which second factor to use. Then, get
                            // the selected hint:
                            //   val selectedHint = multiFactorResolver.hints[selectedIndex] as PhoneMultiFactorInfo
                            // Send the SMS verification code.
                            var number: String
                            val phone_editText: EditText = findViewById(R.id.editTextPhone)
                            number = phone_editText.text.trim().toString()
                            if (number.isNotEmpty()) {
                                if (number.length == 10) {  // EL NUMERO TIENE LA LONGITUD DESEADA
                                    number = "+52$number"

                                    PhoneAuthProvider.verifyPhoneNumber(
                                        PhoneAuthOptions.newBuilder()
                                            .setActivity(this)
                                            .setMultiFactorSession(multiFactorResolver.session)
                                            //   .setMultiFactorHint(selectedHint)
                                            .setCallbacks(callbacks)
                                            .setTimeout(30L, TimeUnit.SECONDS)
                                            .build()
                                    )

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Please Enter a valid Number",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(this, "Please Enter a Number", Toast.LENGTH_SHORT)
                                    .show()
                            }


                        } else {
                            // Handle other errors such as wrong password.
                        }
                    }

            } // ESTE CIERRA CON if (edt.text.isNotEmpty() && . . .
        }

/*
        val btn12: Button = findViewById(R.id.btn_Verify)
        btn12.setOnClickListener {
// Ask user for the SMS verification code, then use it to get
// a PhoneAuthCredential:
            val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)

// Initialize a MultiFactorAssertion object with the
// PhoneAuthCredential.
            val multiFactorAssertion: MultiFactorAssertion =
                PhoneMultiFactorGenerator.getAssertion(credential)

// Complete sign-in.
            multiFactorResolver.resolveSignIn(multiFactorAssertion).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User successfully signed in with the
                    // second factor phone number.
                }
                // ...
            }
        }
        */

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
        //println("Stopping 2 seconds...")
        try {
            // sleep for one second
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        //println("Resuming…")
    }
    private fun verifyEmail(user:FirebaseUser?){
        println("Entró a verified EMAIL user")
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

                        for (i in 1..30){
                            waiting()
                            println("esperando 30 segundos")
                        }

            }
    }


   private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1) Instant verification. In some cases, the phone number can be
            //    instantly verified without needing to send or enter a verification
            //    code. You can disable this feature by calling
            //    PhoneAuthOptions.builder#requireSmsValidation(true) when building
            //    the options to pass to PhoneAuthProvider#verifyPhoneNumber().
            // 2) Auto-retrieval. On some devices, Google Play services can
            //    automatically detect the incoming verification SMS and perform
            //    verification without user action.

           //this@pantallaMail.credential = credential
            println("FUE onVerificationCompleted")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in response to invalid requests for
            // verification, like an incorrect phone number.
            println("FUE onVerificationFailed")
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }
            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(
            verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number.
            // We now need to ask the user to enter the code and then construct a
            // credential by combining the code with a verification ID.
            // Save the verification ID and resending token for later use.

            //this@pantallaMail.verificationId = OTP
            //this@pantallaMail.forceResendingToken = ResendToken
            println("FUE OnCodeSent")

            val intent = Intent(this@pantallaMail, pantallaMail::class.java) //MINUTO 17:20
            intent.putExtra("OTP", verificationId)
            intent.putExtra("RESENDTOKEN", forceResendingToken)
            intent.putExtra("phoneNumber", number)
            intent.putExtra("control", control)
            startActivity(intent)


            // ...
        }
    }
    /*
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            // Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }
        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            // Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Log.d(TAG, "onCodeSent:$verificationId")
            // Save verification ID and resending token so we can use them later
            // storedVerificationId = verificationId
            // resendToken = token
            val intent = Intent(this@pantallaMail, OtpVerificationActivity::class.java) //MINUTO 17:20
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)
            startActivity(intent)
            mProgressBar.visibility = View.INVISIBLE

        }
    }
*/
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
