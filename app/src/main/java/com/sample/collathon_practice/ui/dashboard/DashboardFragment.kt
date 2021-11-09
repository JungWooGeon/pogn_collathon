package com.sample.collathon_practice.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.sample.collathon_practice.AddActivity
import com.sample.collathon_practice.R
import com.sample.collathon_practice.databinding.FragmentDashboardBinding
import com.sample.collathon_practice.model.User
import com.sample.collathon_practice.ListAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    var UserList = arrayListOf<User>(
        User(R.drawable.ic_home_black_24dp,"한국형수달","korean-otter@naver.com","hello world!!!"),
        User(R.drawable.ic_home_black_24dp,"김수연","jmssk11@naver.com","왜 먹어도 먹어도 배가 고픈 것일까"),
        User(R.drawable.ic_home_black_24dp,"임채원","hello@gmail.com","으이이익"),
        User(R.drawable.ic_home_black_24dp,"물고기","asdf@naver.com","ㅁㄴㅇㄹasdfasdfasdfasdfasdfasdfasdfasdfㅁㄴㅇㄹas d f a s df as d fa s d f a s d f as d f a s d f a s df"),
        User(R.drawable.ic_home_black_24dp,"도마뱀","qwer@naver.com","ㅁㄴㅇㄹasdfasdfasdfㅁㄴㅇㄹasdfasdfasdfㅁㄴㅇㄹasdfasdfasdfㅁㄴㅇㄹasdfasdfasdf"),
        User(R.drawable.ic_home_black_24dp,"멈무이","zxcv@naver.com","보자보자 으디보자"),
        User(R.drawable.ic_home_black_24dp,"냥이","cat@naver.com","넌 두고보자"),
        User(R.drawable.ic_home_black_24dp,"집사님","asdfasdf@naver.com","보자보자 으디보자"),
        User(R.drawable.ic_home_black_24dp,"otter","otter66@kakao.com","넌 다음에 보자")
    )

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

        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        var Adapter = ListAdapter(activity, UserList)
        var list = view.findViewById<ListView>(R.id.list_view)
        list.adapter = Adapter

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
}
