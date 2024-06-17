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
import com.siegengel.ping_fct.Notifications.APIService
import com.siegengel.ping_fct.Notifications.Client
import com.siegengel.ping_fct.Notifications.Data
import com.siegengel.ping_fct.Notifications.MyResponse
import com.siegengel.ping_fct.Notifications.Sender
import com.siegengel.ping_fct.Notifications.Token

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query


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

    private lateinit var seenListener: ValueEventListener

    private lateinit var apiService: APIService
    private var notify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_message)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService =
            Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
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
            notify = true
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
                if (user.getImageURL() != null) {
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

        val chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(intent.getStringExtra("userid")!!)
            .child(fuser.uid)
        chatRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    chatRef2.child("id").setValue(fuser.uid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val rfc = FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)
        rfc.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (notify){
                    sendNotifiaction(receiver, user!!.getUsername(), message)
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun sendNotifiaction(receiver: String, username: String?, message: String) {
        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        val query: com.google.firebase.database.Query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val token = snapshot.getValue(Token::class.java)
                    val data = Data(
                        fuser.uid, R.mipmap.ic_launcher,
                        "$username: $message", "New Message",
                        fuser.uid
                    )

                    val sender = Sender(data, token!!.getToken())

                    apiService.sendNotification(sender)
                        ?.enqueue(object : Callback<MyResponse?> {
                            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(this@MessageActivity, "Failed!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse?>, t: Throwable) {
                            }
                        })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
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
                    if (chat != null) {
                        if (chat.getReceiver() == myid && chat.getSender() == userid ||
                            chat.getReceiver() == userid && chat.getSender() == myid
                        ) {
                            if (mChat != null) {
                                chat.setMessage(chat.getMessage()!!)
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