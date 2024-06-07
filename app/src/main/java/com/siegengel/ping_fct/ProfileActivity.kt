package com.siegengel.ping_fct

import android.os.Bundle
import android.util.Log
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

class ProfileActivity : AppCompatActivity() {

    private lateinit var profilepicture: ImageView
    private lateinit var profilegradient: ImageView
    private lateinit var username: TextView

    private lateinit var reference:DatabaseReference
    private lateinit var fuser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }
    private fun initView() {
        profilepicture = findViewById(R.id.main_profile_picture)
        profilegradient = findViewById(R.id.bg_profile_picture)
        username = findViewById(R.id.usernameP)

        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username.text = user!!.getUsername()
                if (user.getImageURL() == "default") {
                    profilepicture.setImageResource(R.drawable.default_profile_picture)
                    profilegradient.setImageResource(R.color.black_olive)
                } else {
                    Glide.with(applicationContext).load(user.getImageURL()).into(profilepicture)
                    Glide.with(applicationContext).load(user.getImageURL()).into(profilegradient)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Failed to read value.", error.toException())
            }
        })
    }
}