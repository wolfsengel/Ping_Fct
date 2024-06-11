package com.siegengel.ping_fct

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.siegengel.ping_fct.Adapter.MessageAdapter
import com.siegengel.ping_fct.Model.Chat
import com.siegengel.ping_fct.Model.User

class MessageActivity : AppCompatActivity() {

    private lateinit var btn_send: Button
    private lateinit var text_send: EditText
    private var MessageAdapter: MessageAdapter? = null
    private var mChat: ArrayList<Chat>? = null
    private lateinit var conversation: RecyclerView

    private lateinit var profilepicture: ImageView
    private lateinit var username: TextView
    private lateinit var fuser: FirebaseUser
    private lateinit var reference: DatabaseReference
    private lateinit var intent: Intent

    private lateinit var seenListener:ValueEventListener;
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
        conversation = findViewById(R.id.conversation)
        conversation.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        conversation.layoutManager = linearLayoutManager

        btn_send = findViewById(R.id.send_btn)
        fuser = FirebaseAuth.getInstance().currentUser!!
        btn_send.setOnClickListener {
            val msg = text_send.text.toString()
            if (msg != "") {
                sendMessage(fuser.uid, intent.getStringExtra("userid")!!, msg)
            }
            text_send.setText("")
        }

        text_send = findViewById(R.id.message_send)
        profilepicture = findViewById(R.id.Muserpicture)
        username = findViewById(R.id.Musername)
        intent = getIntent()
        val userid = intent.getStringExtra("userid")
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username.text = user!!.getUsername()
                if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null) {
                    profilepicture.setImageResource(R.drawable.default_profile_picture)
                } else {
                    Glide.with(applicationContext).load(user.getImageURL()).into(profilepicture)
                }
                var quemaltodo = "default"
                if (user.getImageURL() != null){
                    quemaltodo = user.getImageURL()!!
                }
                readMessages(fuser.uid, userid, quemaltodo)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        seenMessage(userid)
    }

    private fun seenMessage(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snap in snapshot.children) {
                    val chat = snap.getValue(Chat::class.java)
                    if (chat!!.getReceiver() == fuser.uid && chat.getSender() == userid) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snap.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun sendMessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().getReference()
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["isseen"] = false
        reference.child("Chats").push().setValue(hashMap)

        // Add user
        val chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(fuser.uid)
            .child(intent.getStringExtra("userid")!!)
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(intent.getStringExtra("userid")!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun readMessages(myid: String, userid: String, imageurl: String) {
        mChat = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mChat!!.clear()
                for (snap in snapshot.children) {
                    val chat = snap.getValue(Chat::class.java)
                    if(chat != null){
                        if (chat.getReceiver() == myid && chat.getSender() == userid ||
                            chat.getReceiver() == userid && chat.getSender() == myid
                        ) {
                            if (mChat != null) {
                                mChat!!.add(chat)
                            }
                        }
                    }
                    MessageAdapter = MessageAdapter(this@MessageActivity, mChat!!, imageurl)
                    conversation.adapter = MessageAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    private fun status(status: String) {
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
        if (this::seenListener.isInitialized) {
            reference.removeEventListener(seenListener)
        }
        status("offline")
    }
}