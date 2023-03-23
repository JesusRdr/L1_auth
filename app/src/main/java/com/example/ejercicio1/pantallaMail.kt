package com.example.ejercicio1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
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
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.google.firebase.auth.FirebaseAuthMultiFactorException
//import com.google.firebase.auth.databinding.FragmentMultiFactorSignInBinding



//import com.google.firebase.quickstart.auth.databinding.FragmentMultiFactorSignInBinding

import java.util.Objects

// import kotlinx.android.synthetic.main.activity_pantalla_mail.*
private var control: Number = 1

class pantallaMail : AppCompatActivity() {
/*
    private var _binding: FragmentMultiFactorSignInBinding? = null
    private val binding: FragmentMultiFactorSignInBinding
        get() = _binding!!
*/
    private lateinit var RESENDTOKEN: PhoneAuthProvider.ForceResendingToken
    private lateinit var OTP: String

    private lateinit var number: String
    private lateinit var auth: FirebaseAuth

    //private lateinit var multiFactorResolver: MultiFactorResolver

    private var lastPhoneAuthCredential: PhoneAuthCredential? = null
    private var lastVerificationId: String? = null

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
            }else if(edt.text.isNotEmpty() && edt2.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(edt.text.toString(), edt2.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@pantallaMail, "Single Email - Password account Registered", Toast.LENGTH_LONG).show()
                        waiting()
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    }}
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
                    Toast.makeText(this@pantallaMail, "MFA Enroll completed. Go to SIGN IN...", Toast.LENGTH_LONG).show()

                }else {
                    Toast.makeText(this,"Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Constructor para hacer Login. YA DEBE ESTAR LLENO EL NUMERO DE TELEFONO PARA HACER EL LOGIN
                /*
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
                                checkIfEmailVerified(user)
                                val user = FirebaseAuth.getInstance().currentUser

                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                                //     return@addOnCompleteListener
                               // showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
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
            */

        // Constructor para hacer Login
        val btn3: Button = findViewById(R.id.btnIn)
        btn3.setOnClickListener {
            val edt: EditText = findViewById<EditText>(R.id.editTextTextEmailAddress)
            val edt2: EditText = findViewById<EditText>(R.id.editTextTextPassword)
            val edt3: EditText = findViewById<EditText>(R.id.editPhoneMFA)
            if (edt.text.isNotEmpty() && edt2.text.isNotEmpty() && edt3.text.isNotEmpty()){
                //checkIfEmailVerified(user)

                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.text.toString(),edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        // USER NOT ENROLLED WITH MFA
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }
                    if (it.exception is FirebaseAuthMultiFactorException) { // EN LUGAR DE IT, Tenía "task"
                        val multiFactorResolver =
                            (it.exception as FirebaseAuthMultiFactorException).resolver

                        val phone_editText2: EditText = findViewById(R.id.editPhoneMFA)
                        number = phone_editText2.text.trim().toString()
                        number = "+52$number"

                        // Ask user which second factor to use. Then, get
                        // the selected hint:
                        val selectedIndex = 0
                        val selectedHint =
                            multiFactorResolver.hints[selectedIndex] as PhoneMultiFactorInfo
                        //val selectedHint = multiFactorResolver.hints as PhoneMultiFactorInfo

                        // Send the SMS verification code.
                        PhoneAuthProvider.verifyPhoneNumber(
                            PhoneAuthOptions.newBuilder(auth)
                                .setActivity(this)
                                .setMultiFactorSession(multiFactorResolver.session)
                                .setMultiFactorHint(selectedHint)
                                .setCallbacks(generateCallbacks())
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .build()

                        )
                        Toast.makeText(this@pantallaMail, "SMS Code sent", Toast.LENGTH_SHORT).show()

/*
                        val edit_CODE: EditText = findViewById(R.id.editTextCODE)
                        edit_CODE.visibility = View.VISIBLE
                        val verification_val2: Button = findViewById(R.id.btn_Verify2)
                        verification_val2.visibility = View.VISIBLE
*/ //ENSEÑA LOS BOTONES QUE REQUIERO

                        OTP = intent.getStringExtra("OTP").toString()
                        println("antes del DIALOG")
                        println("OTP")
                        println(OTP)
              // AQUI EMPIEZA EL ALERT DIALOG

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Ingrese su codigo OTP")
                        //val view = layoutInflater.inflate(R.layout.dialog, null)

                        val input = EditText(this)
                        builder.setView(input)
                        //builder.setView(view) // PASAR LA VISTA AL BUILDER

                        builder.setPositiveButton("OK"){_, _ ->
                            // Aquí se ejecuta el código después de que el usuario introduce el código y hace clic en Aceptar
                            println("Aquí se ejecuta el código después de que el usuario introduce el código y hace clic en Aceptar")
                            println("-")
                            val nombre = input.text.toString()
                            println("si pasó al ok")
                            println(nombre)
                            println("-")
                            println("Continúa con el flujo del programa aquí")

                                    val OTP_CODE_typed = nombre;
                                    if (OTP_CODE_typed.isNotEmpty()) {
                                        println("si entró al primer IF (NOT EMPTY)")
                                        if (OTP_CODE_typed.length == 6) {
                                            println("si entró al SEGUNDO IF == 6")
                                            // Ask user for the SMS verification code, then use it to get
                                            // a PhoneAuthCredential:
                                            val credential = PhoneAuthProvider.getCredential(OTP, OTP_CODE_typed)
                                            // Initialize a MultiFactorAssertion object with the
                                            // PhoneAuthCredential.
                                            val multiFactorAssertion: MultiFactorAssertion =
                                                PhoneMultiFactorGenerator.getAssertion(credential)
                                            //if (::multiFactorResolver.isInitialized) {
                                            // Complete sign-in.
                                            // val multiFactorResolver = (it.exception as FirebaseAuthMultiFactorException).resolver

                                            multiFactorResolver.resolveSignIn(multiFactorAssertion)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                        Toast.makeText(this,"Double Factor succeeded, WELCOME!", Toast.LENGTH_LONG).show()
                                                        val intent: Intent = Intent(this, Logged_screen::class.java)
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(this,"Double Factor entered Wrong, sorry", Toast.LENGTH_LONG).show()
                                                    }
                                                    // ...
                                                }
                                        } else {
                                            Toast.makeText(this,"Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                                        }
                                    }else {
                                        Toast.makeText(this,"Please Enter OTP", Toast.LENGTH_SHORT).show()
                                    }

                                    println("Salió del dialog")
                                    println("sigue afuera")

                        }
                        builder.setNegativeButton("Cancelar"){
                            dialog,which ->
                            dialog.cancel()
                            println("si pasó al CANCELAR")

                        }
                        val dialog = builder.create()
                        dialog.setOnDismissListener{
                            //val nombre = input.text.toString()
                            println("Entró al setOnDismiss")
                            //println(nombre)

                        }
                        dialog.show()


                        /*
                        val dialog = builder.create() // CREANDO EL DIALOG
                        dialog.show()
                        val cajaOTP: String = view.findViewById<View?>(R.id.caja_OTP_VERIFY).toString()
                        val cajaBTN: Button = view.findViewById(R.id.cajaBTN_VERIFY)
                                cajaBTN.setOnClickListener{
                                    if (cajaOTP.isNotEmpty()){
                                        // val cajaOTP: String = findViewById(R.id.caja_OTP_VERIFY).toString()
                                        Toast.makeText(this,"THANKS",Toast.LENGTH_LONG).show()
                                        println("pasó el if del dialog")
                                        println(cajaOTP)
                                        dialog.hide()
                                    }else{
                                        Toast.makeText(this,"Datos Incorrectos",Toast.LENGTH_LONG).show()
                                    }
                                }
                        */
                        // val edit_verification_val: EditText = findViewById(R.id.editTextCODE)
                        // val OTP_CODE_typed = edit_verification_val.text.toString()
              // AQUI TERMINA EL ALERT DIALOG

                    } else {
                        // Handle other errors such as wrong password.
                        println("Algo metiste mal para mandar el mensaje")
                    }
                }  // CIERRA EL TASK  FirebaseAuth.getInstance()...
            }else if(edt.text.isNotEmpty() && edt2.text.isNotEmpty()){
                println(edt)
                println(edt2)
                FirebaseAuth.getInstance().signInWithEmailAndPassword(edt.text.toString(),edt2.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        // USER NOT ENROLLED WITH MFA
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }}
                Toast.makeText(this,"Getting in...", Toast.LENGTH_SHORT).show()
            }
            }


/*
        val btn16: Button = findViewById(R.id.btn_Verify2)
        btn16.setOnClickListener {
            OTP = intent.getStringExtra("OTP").toString()
            val edit_verification_val: EditText = findViewById(R.id.editTextCODE)
            val OTP_CODE_typed = edit_verification_val.text.toString()

            if (OTP_CODE_typed.isNotEmpty()) {
                println("si entró al primer IF (NOT EMPTY)")
                if (OTP_CODE_typed.length == 6) {
                    println("si entró al SEGUNDO IF == 6")
                    // Ask user for the SMS verification code, then use it to get
                    // a PhoneAuthCredential:

                    val credential =
                        PhoneAuthProvider.getCredential(OTP, OTP_CODE_typed)
                    // Initialize a MultiFactorAssertion object with the
                    // PhoneAuthCredential.
                    val multiFactorAssertion: MultiFactorAssertion =
                        PhoneMultiFactorGenerator.getAssertion(credential)
                    //if (::multiFactorResolver.isInitialized) {
                        // Complete sign-in.
                    // val multiFactorResolver = (it.exception as FirebaseAuthMultiFactorException).resolver

                        multiFactorResolver.resolveSignIn(multiFactorAssertion)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val edt: EditText =
                                        findViewById<EditText>(R.id.editTextTextEmailAddress)
                                    val edt2: EditText =
                                        findViewById<EditText>(R.id.editTextTextPassword)
                                    println("ENTRó A AUTENTICAR")
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                                        edt.text.toString(),
                                        edt2.text.toString()
                                    ).addOnCompleteListener {
                                        showHome(it.result?.user?.email ?: "", ProviderType.PRO)
                                    }


                                } else {
                                    println("NO SE PORQUE, PERO JUSTO ANTES DE AUTENTICAR.... NO AUTENTICó")
                                }
                                // ...
                            }
                   // }else{println("NO ESTÁ INICIALIZADA LA VARIABLE:   multiFactorResolver")}

                } else {
                    Toast.makeText(this,"Please Enter Valid OTP", Toast.LENGTH_SHORT).show()

                }
            }else {
                Toast.makeText(this,"Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }*/
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
                    Toast.makeText(this,"Email sent, please in 30 seconds", Toast.LENGTH_LONG).show()
                    Toast.makeText(this,"Email sent, please in 30 seconds", Toast.LENGTH_LONG).show()
                    //showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                }
                else{
                    Toast.makeText(this,"An error has occurred while sending email", Toast.LENGTH_LONG).show()
                }

                for (i in 1..30){
                    waiting()
                    println("ESPERE")
                    println(31-i)

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
            Toast.makeText(this@pantallaMail, "SMS Code sent", Toast.LENGTH_LONG).show()
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

    private fun generateCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                lastPhoneAuthCredential = phoneAuthCredential
                //binding.finishMfaSignIn.performClick()
                Toast.makeText(this@pantallaMail, "Verification complete!", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                lastVerificationId = verificationId
                OTP = verificationId
                //binding.finishMfaSignIn.isClickable = true
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@pantallaMail, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getResolverFromArguments(arguments: Bundle): MultiFactorResolver {
        return arguments.getParcelable(EXTRA_MFA_RESOLVER)!!
    }
    companion object {
        private const val KEY_VERIFICATION_ID = "key_verification_id"
        const val EXTRA_MFA_RESOLVER = "EXTRA_MFA_RESOLVER"
    }
}
