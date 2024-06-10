package com.siegengel.ping_fct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.siegengel.ping_fct.Adapter.UserAdapter
import com.siegengel.ping_fct.Model.Chat
import com.siegengel.ping_fct.Model.User

class MainActivity : AppCompatActivity() {
    private lateinit var profilePicture:  ImageView
    private lateinit var profileName: TextView

    private lateinit var fUser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var recyclerUser: RecyclerView
    private lateinit var usersBtn: ImageView
    private lateinit var settingsBtn: ImageView

    private lateinit var mUsers: ArrayList<User>
    private lateinit var usersList: ArrayList<String>


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
        fUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                profileName.text = user!!.getUsername()
                if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null) {
                    profilePicture.setImageResource(R.drawable.default_profile_picture)
                } else {
                    Glide.with(applicationContext).load(user.getImageURL()).into(profilePicture)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        usersBtn.setOnClickListener {
            val intent = intent.setClass(this@MainActivity, UsersActivity::class.java)
            startActivity(intent)
        }
        settingsBtn.setOnClickListener {
            val intent = intent.setClass(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        profilePicture = findViewById(R.id.userpicture)
        profileName = findViewById(R.id.username)
        recyclerUser = findViewById(R.id.ContactsRecycler)
        recyclerUser.setHasFixedSize(true)
        recyclerUser.layoutManager = LinearLayoutManager(this)

        fUser = FirebaseAuth.getInstance().currentUser!!

        usersList = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (snap in snapshot.children) {
                    val chat = snap.getValue(Chat::class.java)!!
                    if (chat.getSender().equals(fUser.uid)) {
                        usersList.add(chat.getReceiver()!!)
                    }
                    if (chat.getReceiver().equals(fUser.uid)) {
                        usersList.add(chat.getSender()!!)
                    }
                }
                readChats()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })

        usersBtn = findViewById(R.id.usersBtn)
        settingsBtn = findViewById(R.id.settingsBtn)
    }

    private fun readChats() {
        mUsers = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUsers.clear()
                for (snap in snapshot.children) {
                    val user = snap.getValue(User::class.java)

                    for (id in usersList) {
                        if (user!!.getId() == id) {
                            if (mUsers.none { it.getId() == user.getId() }) {
                                mUsers.add(user)
                            }
                        }
                    }
                }
                userAdapter = UserAdapter(this@MainActivity, mUsers, true)
                recyclerUser.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })
    }

    private fun status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}