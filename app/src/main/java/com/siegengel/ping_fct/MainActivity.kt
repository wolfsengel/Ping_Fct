package com.siegengel.ping_fct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.*
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var profilePicture:  ImageView
    private lateinit var profileName: TextView

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                profileName.text = user!!.getUsername()
                if (user.getImageURL() == "default") {
                    profilePicture.setImageResource(R.drawable.default_profile_picture)
                } else {
                    Glide.with(this@MainActivity).load(user.getImageURL()).into(profilePicture)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })
    }

    private fun initViews() {
        profilePicture = findViewById(R.id.profilePicture)
        profileName = findViewById(R.id.profileName)
    }
}