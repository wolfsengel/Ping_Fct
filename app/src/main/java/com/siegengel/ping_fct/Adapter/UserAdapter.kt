package com.siegengel.ping_fct.Adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.siegengel.ping_fct.MessageActivity
import com.siegengel.ping_fct.Model.User
import com.siegengel.ping_fct.R

class UserAdapter(private val mContext: Context, private val mUsers: List<User>, private val isChat: Boolean) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.contact_name)
        var profilePicture: ImageView = itemView.findViewById(R.id.contact_picture)
        var imageStatusOn: ImageView = itemView.findViewById(R.id.contact_status_on)
        var imageStatusOff: ImageView = itemView.findViewById(R.id.contact_status_off)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(mContext, R.layout.item_user, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUsers[position]
        holder.username.text = user.getUsername()
        if (user.getImageURL() == "default" || user.getImageURL() == "" || user.getImageURL() == null){
            holder.profilePicture.setImageResource(R.drawable.default_profile_picture)
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profilePicture)
        }

        if (isChat) {
            if (user.getStatus() == "online") {
                holder.imageStatusOn.visibility = View.VISIBLE
                holder.imageStatusOff.visibility = View.GONE
            } else {
                holder.imageStatusOn.visibility = View.GONE
                holder.imageStatusOff.visibility = View.VISIBLE
            }
        } else {
            holder.imageStatusOn.visibility = View.GONE
            holder.imageStatusOff.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, MessageActivity::class.java)
            intent.putExtra("userid", user.getId())
            mContext.startActivity(intent)
        }
    }
}