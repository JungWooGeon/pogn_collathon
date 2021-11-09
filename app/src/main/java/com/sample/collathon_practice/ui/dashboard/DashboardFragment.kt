package com.sample.collathon_practice.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.sample.collathon_practice.AddActivity
import com.sample.collathon_practice.R
import com.sample.collathon_practice.TimeCapsule
import com.sample.collathon_practice.databinding.FragmentDashboardBinding
import com.sample.collathon_practice.model.User
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.item_timecapsule.view.*
import kotlinx.android.synthetic.main.item_user.view.*
import java.io.File

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    /*
    var UserList = arrayListOf<User>(
        User(R.drawable.logo,"한국형수달","hello world!!!"),
        User(R.drawable.logo,"김수연","왜 먹어도 먹어도 배가 고픈 것일까"),
        User(R.drawable.logo,"임채원","으이이익"),
        User(R.drawable.logo,"물고기","ㅁㄴㅇㄹasdfasdfasdfasdfasdfasdfasdfasdfㅁㄴㅇㄹas d f a s df as d fa s d f a s d f as d f a s d f a s df"),
        User(R.drawable.logo,"도마뱀","ㅁㄴㅇㄹasdfasdfasdfㅁㄴㅇㄹasdfasdfasdfㅁㄴㅇㄹasdfasdfasdfㅁㄴㅇㄹasdfasdfasdf"),
        User(R.drawable.logo,"멈무이","보자보자 으디보자"),
        User(R.drawable.logo,"냥이","넌 두고보자"),
        User(R.drawable.logo,"집사님","보자보자 으디보자"),
        User(R.drawable.logo,"otter","넌 다음에 보자")
    )
    */

    val db = Firebase.firestore
    val storage = Firebase.storage
    var user_family = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val user = Firebase.auth.currentUser
        if(user != null) {
            db?.collection("users").document(user.uid).get()
                .addOnSuccessListener { result ->
                    user_family = result.data?.get("family_id").toString().trim()

                    var recyclerView: RecyclerView = binding.recycleDashboard
                    recyclerView.adapter = DashboardAdapter()
                    recyclerView.layoutManager = LinearLayoutManager(getActivity())
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.goAdd.setOnClickListener {
            activity?.let{
                val intent = Intent(context, AddActivity::class.java)
                startActivity(intent)
            }
        }
    }

    inner class DashboardAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var datas : ArrayList<User> = arrayListOf()

        init {
            db?.collection("family").document(user_family)
                .collection("posts").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    // Clear ArrayList
                    datas.clear()

                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(User::class.java)
                        var title = item?.title.toString().trim()
                        var content = item?.content.toString().trim()

                        var image = item?.image.toString().trim()

                        if(image == "") {
                            // image가 없을 때

                        } else {
                            if(title != null && content != null) {
                                var data = User(image, title, content)
                                datas.add(data)
                            }
                        }
                    }
                    notifyDataSetChanged()
                }
        }
        // item_user.xml을 inflate
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        // connect view with real data
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView


            if(datas[position]?.image != "") {
                // dashboard image upload
                val imgRef = storage.reference.child(user_family+"/"+datas[position]?.image+".jpg")
                imgRef.downloadUrl.addOnSuccessListener {
                    // if upload failed, upload logo.png
                    Glide.with(context!!).load(it).error(R.drawable.logo).into(viewHolder.iv_profileImage)
                }

            }
            viewHolder.name_tv.text = datas[position]?.title
            viewHolder.content_tv.text = datas[position]?.content
        }

        override fun getItemCount(): Int { // size
            return datas.size
        }
    }
}
