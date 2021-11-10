package com.sample.collathon_practice.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sample.collathon_practice.MainActivity
import com.sample.collathon_practice.R
import com.sample.collathon_practice.TimeCapsule
import com.sample.collathon_practice.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.item_timecapsule.view.*
import java.time.LocalDate
import java.time.LocalTime
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val db = Firebase.firestore
    var user_family=""
    var family_name=""
    var family_member:ArrayList<Any?> = arrayListOf<Any?>()
    
    private val binding get() = _binding!!

    private val feels_img = hashMapOf(
        "0" to R.drawable.happy,
        "1" to R.drawable.sad,
        "2" to R.drawable.bad,
        "3" to R.drawable.angry,
        "4" to R.drawable.surprised,
        "5" to R.drawable.uncomfortable
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        //get family info
        var name=root.findViewById<TextView>(R.id.setting_name)

        var user = Firebase.auth.currentUser!!

        if (user.uid != null){
            var docRef = db.collection("users").document(user.uid)
            docRef.get().addOnSuccessListener { result ->
                var familyuid=result.data?.get("family_id") as String

                println("familyuid"+familyuid)
                if (familyuid != null) {
                    var current_time= LocalDate.now()
                    var current_year:Int=current_time.year
                    var current_month:Int=current_time.monthValue
                    var current_day:Int=current_time.dayOfMonth
                    var current_tt= LocalTime.now()
                    var current_hour:Int=current_tt.hour
                    var current_m:Int=current_tt.minute
                    println("현재시간:"+current_time+" "+ current_year+" "+current_month+" "+current_day+" "+current_tt+" "+current_hour+" "+current_m)

                    db?.collection("family").document(familyuid)
                        .collection("posts").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            // Clear ArrayList
                            for (snapshot in querySnapshot!!.documents) {
                                var documentid=snapshot.id.toString()

                                var time=snapshot.data?.get("time").toString()
                                var temp=time.split("\t")
                                var year_month_day=temp[0].trim().split("/")
                                var document_time=temp[1].trim().split(":")
                                println("test:"+document_time[0]+" "+document_time[1] + "test2:"+current_hour+" "+current_m)

                                var flag=false
                                if(year_month_day[0].toInt()<current_year){ //year
                                    flag=true
                                }
                                else if(year_month_day[0].toInt()==current_year){ //same year
                                    println("same year")
                                    if(year_month_day[1].toInt()<current_month){ //month
                                        flag=true
                                    }
                                    else if(year_month_day[1].toInt()==current_month){ //same month
                                        println("same month")
                                        if(year_month_day[2].toInt()<current_day){ //day
                                            flag=true
                                        }
                                        else if(year_month_day[2].toInt()==current_day){ //same day
                                            println("same day")
                                            if(document_time[0].toInt()<current_hour){
                                                flag=true
                                            }
                                            else if(document_time[0].toInt()==current_hour){ //same hour
                                                println("same hour")
                                                println("min:"+document_time[1].toInt()+" "+current_m)
                                                if(document_time[1].toInt()<current_m){
                                                    flag=true
                                                }
                                            }
                                        }
                                    }
                                }
                                if(flag==true){
                                    println("삭제:"+documentid)
                                    db.collection("family").document(familyuid).collection("posts").document(documentid).delete()
                                }

                            }
                        }
                }

            }
        }


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

                                            var userfeel = result2.data?.get("feel").toString()
                                            var iv = root.findViewById<ImageView>(resources.getIdentifier("feel_"+k, "id", "com.sample.collathon_practice"))

                                            var feel = feels_img
                                            feel[userfeel]?.let {
                                                iv.setImageResource(it)
                                            }

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var familyuid=(getActivity() as MainActivity).familyuid

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