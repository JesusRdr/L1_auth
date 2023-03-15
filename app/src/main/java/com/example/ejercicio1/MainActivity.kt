package com.example.ejercicio1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn: Button = findViewById(R.id.btn_mail)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, pantallaMail:: class.java)
            startActivity(intent)
            }
        }

}
