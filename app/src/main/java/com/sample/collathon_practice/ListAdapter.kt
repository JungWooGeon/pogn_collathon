package com.sample.collathon_practice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sample.collathon_practice.model.User

class ListAdapter(val context: Context, val UserList: ArrayList<User>) : BaseAdapter() {
    override fun getCount(): Int {
        return UserList.size
    }

    override fun getItem(position: Int): Any {
        return UserList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user, null)
        val profile = view.findViewById<ImageView>(R.id.iv_profileImage)
        val name = view.findViewById<TextView>(R.id.name_tv)
        val email = view.findViewById<TextView>(R.id.email_tv)
        val content = view.findViewById<TextView>(R.id.content_tv)
        val user = UserList[position]

        profile.setImageResource(user.profile)
        name.text = user.name
        email.text = user.email
        content.text = user.content

        return view
    }
}