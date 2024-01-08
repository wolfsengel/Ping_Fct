package com.siegengel.ping_fct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private  lateinit var regBtn: Button
    private  lateinit var regEmail: EditText
    private  lateinit var regPassword: EditText
    private  lateinit var regConfirmPassword: EditText
    private lateinit var regUsername: EditText
    private lateinit var alrd_reg: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initViews()

        auth = FirebaseAuth.getInstance()

        regBtn.setOnClickListener{
            val email = regEmail.text.toString()
            val password = regPassword.text.toString()
            val confirmPassword = regConfirmPassword.text.toString()
            val username =  regUsername.text.toString()
            if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()){
                Toast.makeText(this, R.string.emptyFields, Toast.LENGTH_SHORT).show()
            }else {
                if (password == confirmPassword) {
                    registerUser(email, password, username)
                } else {
                    Toast.makeText(this, R.string.wrongPassword, Toast.LENGTH_SHORT).show()
                }
            }
        }
        alrd_reg.setOnClickListener{
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun registerUser(email:String, password:String, username:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    val user: FirebaseUser? = auth.currentUser
                    val uid:String = user!!.uid
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(uid)

                    val hashMap:HashMap<String, String> = HashMap()
                    hashMap.put("id", uid)
                    hashMap.put("username", username)
                    hashMap.put("profileImage", "default")

                    reference.setValue(hashMap).addOnCompleteListener(this){task2->
                        if(task2.isSuccessful){
                            Toast.makeText(this, R.string.goodReg, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "se mamo el hash", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, R.string.wrongRegister, Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun initViews(){
        regBtn = findViewById(R.id.registerButton)
        regEmail = findViewById(R.id.emailTextReg)
        regPassword = findViewById(R.id.passwordText1Reg)
        regConfirmPassword = findViewById(R.id.passwordText2Reg)
        regUsername = findViewById(R.id.usernameReg)
        alrd_reg = findViewById(R.id.alrd_reg_btn)
    }

}