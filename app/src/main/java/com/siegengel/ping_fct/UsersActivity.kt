package com.siegengel.ping_fct

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siegengel.ping_fct.Adapter.UserAdapter
import com.siegengel.ping_fct.Model.User

class UsersActivity : AppCompatActivity() {
    private lateinit var recyclerUser: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var mUsers: List<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        loadContacts()
    }

    private fun initViews() {
        recyclerUser = findViewById(R.id.usersRecycler)
    }

    private fun loadContacts(){
        recyclerUser.setHasFixedSize(true)
        recyclerUser.layoutManager = LinearLayoutManager(this)
        mUsers = ArrayList()
        readUsers()
    }

    private fun readUsers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user!!.getId() != firebaseUser!!.uid) {
                        (mUsers as java.util.ArrayList<User>).add(user)
                    }
                }
                userAdapter = UserAdapter(this@UsersActivity, mUsers)
                recyclerUser.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })

    }
}