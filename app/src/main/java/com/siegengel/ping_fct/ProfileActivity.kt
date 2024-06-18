package com.siegengel.ping_fct

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import java.util.Locale


class ProfileActivity : AppCompatActivity() {

    private lateinit var profilegradient: ImageView
    private lateinit var username: EditText
    private lateinit var logout: Button
    private lateinit var erase: Button
    private lateinit var editbtn: ImageView
    private lateinit var editbtn2: ImageView
    private lateinit var backbtn: ImageView

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
        backbtn = findViewById(R.id.backBtnprofile)
        editbtn = findViewById(R.id.editbtn)
        editbtn2 = findViewById(R.id.editbtn2)
        profilegradient = findViewById(R.id.bg_profile_picture)
        username = findViewById(R.id.usernameP)
        logout = findViewById(R.id.logoutbtn)
        erase = findViewById(R.id.eraseaccountbtn)
        backbtn.setOnClickListener {
            finish()
        }

        editbtn2.setOnClickListener {
            //el texto en username se convierte en el nuevo username
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val updates = hashMapOf<String, Any>(
                    "username" to username.text.toString()
                )
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.uid).updateChildren(updates)
            }
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, StartActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        erase.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val updates = hashMapOf<String, Any>(
                    "username" to "erased account",
                    "profileImage" to "default",
                    "status" to "offline",
                    "search" to "漢語",
                )
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.uid).updateChildren(updates)
            }
            // Delete user from Firebase Authentication
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User account deleted.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, StartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
        }


        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.uid)

        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                //borrar el contenido de username y poner el username del usuario
                username.text.clear()
                username.text.append(user?.getUsername())
                if (user != null) {
                    if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null) {
                        profilegradient.setImageResource(R.drawable.default_profile_picture)
                    } else {
                        Glide.with(applicationContext).load(user.getImageURL()).into(profilegradient)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Failed to read value.", error.toException())
            }
        })

        editbtn.setOnClickListener {
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
        File(imageUri.path!!)
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

}