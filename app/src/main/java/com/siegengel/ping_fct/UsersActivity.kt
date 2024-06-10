package com.siegengel.ping_fct

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.search.SearchBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siegengel.ping_fct.Adapter.UserAdapter
import com.siegengel.ping_fct.Model.User

class UsersActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var recyclerUser: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var mUsers: List<User>
    private lateinit var reference: DatabaseReference
    private lateinit var fuser: FirebaseUser
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
        searchBar = findViewById(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun searchUsers(lowercase: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(lowercase)
            .endAt(lowercase + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user!!.getId() != firebaseUser!!.uid) {
                        (mUsers as java.util.ArrayList<User>).add(user)
                    }
                }
                userAdapter = UserAdapter(this@UsersActivity, mUsers, false)
                recyclerUser.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })
    }

    private fun loadContacts(){
        recyclerUser.setHasFixedSize(true)
        recyclerUser.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
                userAdapter = UserAdapter(this@UsersActivity, mUsers, false)
                recyclerUser.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO()
            }
        })

    }
    private fun status(status: String) {
        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)
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