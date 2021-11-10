package com.sample.collathon_practice.ui.dashboard

import android.content.Intent
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
import com.google.firebase.storage.ktx.storage
import com.sample.collathon_practice.AddActivity
import com.sample.collathon_practice.R
import com.sample.collathon_practice.databinding.FragmentDashboardBinding
import com.sample.collathon_practice.model.Post
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.item_user.*
import kotlinx.android.synthetic.main.item_user.view.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

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

                    db?.collection("family").document(user_family).get().addOnSuccessListener {
                        r->
                        family_title.text = r.data?.get("name").toString().trim()
                    }

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
        var data : ArrayList<Post> = arrayListOf()

        init {
            db?.collection("family").document(user_family)
                .collection("posts").addSnapshotListener { querySnapshot, e ->
                    // Clear ArrayList
                    data.clear()

                    for (snapshot in querySnapshot!!.documents) {
                        var postid = snapshot.id
                        var item = snapshot.toObject(Post::class.java)
                        var title = item?.title.toString().trim()
                        var content = item?.content.toString().trim()

                        var image = item?.image.toString().trim()
                        var like = item?.like
                        if(image == "") {
                            // image가 없을 때

                        } else {
                            if(title != null && content != null && like != null) {
                                var data = Post(postid, image, title, content, like)
                                this.data.add(data)
                            }
                        }
                    }
                    notifyDataSetChanged()
                }
        }
        // item_user.xml을 inflate
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)

            var user = Firebase.auth.currentUser

            view.radioGroup_post.setOnCheckedChangeListener { rg, checkedId ->
                if (user != null){
                    var postid = view.post_id.text as String
                    var docRef = db.collection("family").document(user_family).collection("posts").document(postid) // 게시글 doc 불러옴

                    when (checkedId) {
                        R.id.rb_like -> {
                            Log.d("MY", ":hello it's like")

                            docRef.get().addOnSuccessListener { result ->
                                var likes:ArrayList<Any?> = result.data?.get("like") as ArrayList<Any?>

//                                if (rb_like.isChecked){
//                                    likes.remove(user.uid)
//                                    rb_like.isChecked = false
//                                } else {
//
//                                }
                                if(user.uid !in likes){
                                    likes.add(user.uid)
                                }

                                docRef.update(mapOf(
                                    "like" to likes
                                ))
                            }
                        }
                    } // when
                } // if
            } // setOnCheckedChangeListener
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        // connect view with real data
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView

            if(data[position]?.image != "") {
                // dashboard image upload
                val imgRef = storage.reference.child(user_family+"/"+data[position]?.image+".jpg")
                imgRef.downloadUrl.addOnSuccessListener {
                    // if upload failed, upload logo.png
                    Glide.with(context!!).load(it).error(R.drawable.logo).into(viewHolder.iv_profileImage)
                }
            }

            viewHolder.post_id.text = data[position]?.postid
            viewHolder.name_tv.text = data[position]?.title
            viewHolder.content_tv.text = data[position]?.content
            viewHolder.cnt_like.text = data[position]?.like.size.toString()

            var like_arr = data[position]?.like
            var user = Firebase.auth.currentUser

            if(user?.uid in like_arr){
                viewHolder.rb_like.isChecked = true
            }

        }

        override fun getItemCount(): Int { // size
            return data.size
        }
    }
}