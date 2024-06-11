package com.siegengel.ping_fct

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ResetPwdActivity : AppCompatActivity() {

    private lateinit var reset_btn: Button
    private lateinit var text_email: EditText

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_pwd)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() {
        reset_btn = findViewById(R.id.resetButton)
        text_email = findViewById(R.id.emailTextReset)
        reset_btn.setOnClickListener {
            val email = text_email.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}