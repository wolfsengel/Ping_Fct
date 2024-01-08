package com.siegengel.ping_fct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {
    private lateinit var logInBtn: Button
    private lateinit var logInEmail: EditText
    private lateinit var logInPassword: EditText
    private lateinit var logInRegister: TextView

    private lateinit var auth: FirebaseAuth

    /*override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        initViews()
        auth = FirebaseAuth.getInstance()

        logInBtn.setOnClickListener {
            val email = logInEmail.text.toString()
            val password = logInPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
        logInRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.goodLogin, Toast.LENGTH_SHORT).show()
                    val intent = intent.setClass(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, R.string.wrongLogin, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initViews() {
        logInBtn = findViewById(R.id.loginButton)
        logInEmail = findViewById(R.id.emailTextLog)
        logInPassword = findViewById(R.id.passwordTextLog)
        logInRegister = findViewById(R.id.newUserBtn)
    }
}