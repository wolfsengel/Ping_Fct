package com.siegengel.ping_fct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.siegengel.ping_fct.Adapter.UserAdapter
import com.siegengel.ping_fct.Model.User

class MainActivity : AppCompatActivity() {
    private lateinit var profilePicture:  ImageView
    private lateinit var profileName: TextView

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var recyclerUser: RecyclerView
    private lateinit var usersBtn: ImageView
    private lateinit var settingsBtn: ImageView

    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                profileName.text = user!!.getUsername()
                if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null) {
                    profilePicture.setImageResource(R.drawable.default_profile_picture)
                } else {
                    Glide.with(this@MainActivity).load(user.getImageURL()).into(profilePicture)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })
        usersBtn.setOnClickListener {
            val intent = intent.setClass(this@MainActivity, UsersActivity::class.java)
            startActivity(intent)
        }
        settingsBtn.setOnClickListener {
            //val intent = intent.setClass(this@MainActivity, SettingsActivity::class.java)
            //startActivity(intent)
        }
    }

    private fun initViews() {
        profilePicture = findViewById(R.id.userpicture)
        profileName = findViewById(R.id.username)
        recyclerUser = findViewById(R.id.ContactsRecycler)
        usersBtn = findViewById(R.id.usersBtn)
        settingsBtn = findViewById(R.id.settingsBtn)
    }
}