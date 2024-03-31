package com.siegengel.ping_fct

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siegengel.ping_fct.Model.User

class MessageActivity : AppCompatActivity() {

    private lateinit var profilepicture:ImageView
    private lateinit var username: TextView
    private lateinit var fuser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var intent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        profilepicture = findViewById(R.id.Muserpicture)
        username = findViewById(R.id.Musername)
        intent = getIntent()
        val userid = intent.getStringExtra("userid")
        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username.text = user!!.getUsername()
                if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null){
                    profilepicture.setImageResource(R.drawable.default_profile_picture)
                } else {
                    Glide.with(this@MessageActivity).load(user.getImageURL()).into(profilepicture)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })
    }
}