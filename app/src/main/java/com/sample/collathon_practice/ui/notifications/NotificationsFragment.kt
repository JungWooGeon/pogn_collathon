package com.sample.collathon_practice.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sample.collathon_practice.LoginActivity
import com.sample.collathon_practice.R
import com.sample.collathon_practice.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getActivity(), LoginActivity::class.java)
            startActivity(intent)
        }

        val user = Firebase.auth.currentUser
        val db = Firebase.firestore
        var userfeel = 0

        //라디오 그룹 설정
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId) {
                R.id.rb_happy -> {
                    // "feel" to 0
                    userfeel = 0
                    Log.d("MY FEEL", "남은 하루도 계속 행복하세요!")
                }
                R.id.rb_sad -> {
                    userfeel = 1
                    Log.d("MY FEEL", "외로워도 슬퍼도 나는 안울어. 하지만 가끔은 울어도 괜찮을지도...")
                }
                R.id.rb_bad -> {
                    userfeel = 2
                    Log.d("MY FEEL", "오늘은 좀 꿀꿀한 기분이야...")
                }
                R.id.rb_angry -> {
                    userfeel = 3
                    Log.d("MY FEEL", "이런 X같은 XXXX. 세상 살다 이런 XX같은 경우가 있나?!!!!")
                }
                R.id.rb_surprised -> {
                    userfeel = 4
                    Log.d("MY FEEL", "아 XX. 놀래라.")
                }
                R.id.rb_uncomfortable -> {
                    userfeel = 5
                    Log.d("MY FEEL", "온몸으로 언짢음을 표현하는 중....")
                }
            }

            if (user != null){
                val docRef = db.collection("users").document(user.uid)

                docRef.update(mapOf(
                    "feel" to userfeel
                )).addOnSuccessListener {
                    Log.d("MY WRITE", "User writes his/her feel")
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}