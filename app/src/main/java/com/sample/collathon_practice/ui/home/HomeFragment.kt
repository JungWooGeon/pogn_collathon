package com.sample.collathon_practice.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sample.collathon_practice.R
import com.sample.collathon_practice.TimeCapsule
import com.sample.collathon_practice.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.item_timecapsule.view.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val db = Firebase.firestore
    var user_family=""
    var family_name=""
    var family_member:ArrayList<Any?> = arrayListOf<Any?>()

    
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        //get family info
        val user = Firebase.auth.currentUser
        if (user != null) {
            db?.collection("users").document(user.uid).get()
                .addOnSuccessListener { result->
                    user_family=result.data?.get("family_id").toString().trim()
                    db?.collection("family").document(user_family).get()
                        .addOnSuccessListener { result->
                            family_name=result.data?.get("name").toString().trim()

                            family_member= result.data?.get("members") as ArrayList<Any?>

                            // family -> 최대 5명 보이도록 설정
                            while(family_member.size<5){
                                family_member.add("0")
                                if(family_member.size>=5){
                                    break
                                }
                            }

                            var k=1
                            for(i in family_member){
                                var temp=""
                                if (i=="0"){
                                    temp="없음"
                                }else{
                                    db?.collection("users").document(i.toString()).get()
                                        .addOnSuccessListener { result2->
                                            temp=result2.data?.get("name").toString().trim()
                                            var x=root.findViewById<TextView>(getResources().getIdentifier("user_"+k,"id","com.sample.collathon_practice"))
                                            x.setText(temp)
                                            k++
                                        }
                                }
                        }


                    var familyname=binding.fmname
                            familyname.text=family_name

                            var recyclerView:RecyclerView=binding.homeRecyclerview
                            recyclerView.adapter = RecyclerViewAdapter()
                            recyclerView.layoutManager = LinearLayoutManager(getActivity())
                        }
                }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var timecapsule : ArrayList<TimeCapsule> = arrayListOf()

        init {
            db?.collection("family").document(user_family)
                .collection("posts").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                // Clear ArrayList
                timecapsule.clear()

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(TimeCapsule::class.java)
                    timecapsule.add(item!!)
                }
                notifyDataSetChanged()
            }
        }

        // item_tiimecapsule.xml을 inflate
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_timecapsule, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        // connect view with real data
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView

            viewHolder.view_title.text = timecapsule[position]?.title
            viewHolder.view_time.text=timecapsule[position]?.time+" 까지"
        }

        override fun getItemCount(): Int { // size
            return timecapsule.size
        }
    }
}