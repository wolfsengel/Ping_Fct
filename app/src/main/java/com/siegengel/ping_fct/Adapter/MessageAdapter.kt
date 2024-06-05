package com.siegengel.ping_fct.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.siegengel.ping_fct.Model.Chat
import com.siegengel.ping_fct.R

class MessageAdapter(private val mContext: Context, private val mChat: List<Chat>, private val imageUrl: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    val MSG_TYPE_LEFT: Int = 0
    val MSG_TYPE_RIGHT: Int = 1

    private var fuser:FirebaseUser? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_message: TextView
        var profilePicture: ImageView

        init {
            show_message = itemView.findViewById(R.id.show_message)
            profilePicture = itemView.findViewById(R.id.profile_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = View.inflate(mContext, R.layout.chat_item_right, null)
            ViewHolder(view)
        } else{
            val view = View.inflate(mContext, R.layout.chat_item_left, null)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        holder.show_message.text = chat.getMessage()
        if (imageUrl == "default") {
            holder.profilePicture.setImageResource(R.drawable.default_profile_picture)
        } else {
            Glide.with(mContext).load(imageUrl).into(holder.profilePicture)
        }
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position].getSender() == fuser!!.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}