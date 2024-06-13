package com.siegengel.ping_fct

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.siegengel.ping_fct.Model.User
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var profilegradient: ImageView
    private lateinit var username: TextView

    private lateinit var reference: DatabaseReference
    private lateinit var fuser: FirebaseUser

    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var uploadTask: StorageReference

    private val openImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data!!
                uploadImage()
            }
        }

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
        profilegradient = findViewById(R.id.bg_profile_picture)
        username = findViewById(R.id.usernameP)

        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)

        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username.text = user!!.getUsername()
                if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null) {
                    profilegradient.setImageResource(R.drawable.default_profile_picture)
                } else {
                    Glide.with(applicationContext).load(user.getImageURL()).into(profilegradient)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Failed to read value.", error.toException())
            }
        })

        profilegradient.setOnClickListener {
            openImage()
        }
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        openImageResultLauncher.launch(intent)
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = cR.getType(uri)
        return mime?.substring(mime.lastIndexOf("/") + 1)
    }

    private fun uploadImage() {
        val file = File(imageUri.path!!)
        if (true) {
            val fileReference = storageReference.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
            )
            uploadTask = fileReference
            uploadTask.putFile(imageUri).addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val mUri = uri.toString()
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)
                    val map = HashMap<String, Any>()
                    map["imageURL"] = mUri
                    reference.updateChildren(map)
                    Log.d("ProfileActivity", "Image uploaded successfully")

                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user?.getImageURL() == "default" || user?.getImageURL() == "" || user?.getImageURL() == null) {
                                profilegradient.setImageResource(R.drawable.default_profile_picture)
                            } else {
                                Glide.with(applicationContext).load(user?.getImageURL()).into(profilegradient)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("ProfileActivity", "Failed to read value.", error.toException())
                        }
                    })
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Failed to upload image", it)
            }
        } else {
            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show()
        }
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
        status("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        status("offline")
    }
}