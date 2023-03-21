package com.example.ejercicio1

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.FirebaseUser

class MfaPhoneActivity : AppCompatActivity() {

    private lateinit var credential: PhoneAuthCredential

    //creo las variables de lo que vamos a usar
    private lateinit var sendOTP_btn : Button
    private lateinit var phone_editText : EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var number : String
    private  lateinit var mProgressBar : ProgressBar

   //private lateinit var usuario : FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mfa_phone)

        title= "MFA Authentication"
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        //usuario = intent.getIntArrayExtra("user").toString()

        init()

        sendOTP_btn.setOnClickListener {
            number = phone_editText.text.trim().toString()
            if (number.isNotEmpty()){
                if (number.length == 10){  // EL NUMERO TIENE LA LONGITUD DESEADA
                    number = "+52$number"
                    mProgressBar.visibility = View.VISIBLE

                    val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
                    user?.multiFactor?.session?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val multiFactorSession = task.result
                                val phoneAuthOptions = PhoneAuthOptions.newBuilder()
                                    .setPhoneNumber(number)
                                    .setTimeout(30L, TimeUnit.SECONDS)
                                    .setMultiFactorSession(multiFactorSession)
                                    .setCallbacks(callbacks)
                                    .build()
                                // Send SMS verification code.
                                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
                            }
                        }


                }else {
                    Toast.makeText(this, "Please Enter a valid Number", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please Enter a Number", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun init(){
        mProgressBar = findViewById(R.id.progressBar_phone)
        mProgressBar.visibility = View.INVISIBLE
        sendOTP_btn = findViewById(R.id.btnOTP)
        phone_editText = findViewById(R.id.editTextPhone)
        auth = FirebaseAuth.getInstance()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    // Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(this, "Authentication succeeded", Toast.LENGTH_SHORT).show()
                    sendToMain()
                    // val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    // Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun sendToMain(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    // SIGUIENTE SECCION COPIADA DE LA DOCUMENTACION DE FIREBASE

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
            val intent = Intent(this@MfaPhoneActivity, OtpVerificationActivity::class.java) //MINUTO 17:20
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)
            startActivity(intent)
            mProgressBar.visibility = View.INVISIBLE

        }
    }
*/

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
            this@MfaPhoneActivity.credential = credential
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in response to invalid requests for
            // verification, like an incorrect phone number.
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
      //      this@MfaPhoneActivity.verificationId = verificationId
      //      this@MfaPhoneActivity.forceResendingToken = forceResendingToken
            // ...
            val intent = Intent(this@MfaPhoneActivity, MfaOtpActivity::class.java) //MINUTO 17:20
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", forceResendingToken)
            intent.putExtra("phoneNumber", number)
            startActivity(intent)
            mProgressBar.visibility = View.INVISIBLE

        }
    }

    override fun onStart(){
        super.onStart()
        if(auth.currentUser != null){
            startActivity(Intent(this, Logged_screen::class.java))
        }
    }
}