package com.example.ejercicio1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var verify_btn : Button
    private lateinit var resend_view : TextView
    private lateinit var inOPT_1 : EditText
    private lateinit var inOPT_2 : EditText
    private lateinit var inOPT_3 : EditText
    private lateinit var inOPT_4 : EditText
    private lateinit var inOPT_5 : EditText
    private lateinit var inOPT_6 : EditText
    private lateinit var progressBar_val : ProgressBar

    private lateinit var OTP : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        title= "OTP"
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        init()
        progressBar_val.visibility = View.INVISIBLE
        addTextChangeListener()
        resendOTPVisibility()

        resend_view.setOnClickListener {
            resendVerificationCode()
            resendOTPVisibility()
        }

        verify_btn.setOnClickListener {
            // collect OTP from all the edit text
            val typedOTP = (inOPT_1.text.toString()+inOPT_2.text.toString()+inOPT_3.text.toString()+inOPT_4.text.toString()+inOPT_5.text.toString()+inOPT_6.text.toString())
            if(typedOTP.isNotEmpty()){
                if(typedOTP.length == 6){
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(OTP,typedOTP)
                    progressBar_val.visibility= View.VISIBLE
                    signInWithPhoneAuthCredential(credential)
                }else {
                    Toast.makeText(this,"Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun resendOTPVisibility(){
        inOPT_1.setText("")
        inOPT_2.setText("")
        inOPT_3.setText("")
        inOPT_4.setText("")
        inOPT_5.setText("")
        inOPT_6.setText("")
        resend_view.visibility = View.INVISIBLE
        resend_view.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            resend_view.visibility = View.VISIBLE
            resend_view.isEnabled = true
        }, 60000 )
    }

    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

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
            OTP = verificationId
            resendToken = token
        }
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
        startActivity(Intent(this, Logged_screen::class.java))
    }

    private fun addTextChangeListener(){
        inOPT_1.addTextChangedListener(EditTextWatcher(inOPT_1))
        inOPT_2.addTextChangedListener(EditTextWatcher(inOPT_2))
        inOPT_3.addTextChangedListener(EditTextWatcher(inOPT_3))
        inOPT_4.addTextChangedListener(EditTextWatcher(inOPT_4))
        inOPT_5.addTextChangedListener(EditTextWatcher(inOPT_5))
        inOPT_6.addTextChangedListener(EditTextWatcher(inOPT_6))
    }
    private fun init(){
        auth = FirebaseAuth.getInstance()
        progressBar_val = findViewById(R.id.progressBarOTP_obj)
        verify_btn = findViewById(R.id.btnVerify)
        resend_view = findViewById(R.id.resendTextView)
        inOPT_1 = findViewById(R.id.editTextNumber)
        inOPT_2 = findViewById(R.id.editTextNumber5)
        inOPT_3 = findViewById(R.id.editTextNumber6)
        inOPT_4 = findViewById(R.id.editTextNumber7)
        inOPT_5 = findViewById(R.id.editTextNumber8)
        inOPT_6 = findViewById(R.id.editTextNumber9)
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
        val text = p0.toString()
            when(view.id){
                R.id.editTextNumber -> if(text.length == 1) inOPT_2.requestFocus()
                R.id.editTextNumber5 -> if(text.length == 1) inOPT_3.requestFocus() else if (text.isNotEmpty()) inOPT_1.requestFocus()
                R.id.editTextNumber6 -> if(text.length == 1) inOPT_4.requestFocus() else if (text.isNotEmpty()) inOPT_2.requestFocus()
                R.id.editTextNumber7 -> if(text.length == 1) inOPT_5.requestFocus() else if (text.isNotEmpty()) inOPT_3.requestFocus()
                R.id.editTextNumber8 -> if(text.length == 1) inOPT_6.requestFocus() else if (text.isNotEmpty()) inOPT_4.requestFocus()
                R.id.editTextNumber9 -> if(text.isEmpty()) inOPT_5.requestFocus()
            }
        }

    }
}