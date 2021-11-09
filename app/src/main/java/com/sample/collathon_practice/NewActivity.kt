package com.sample.collathon_practice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class NewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        val btn_join = this.findViewById<Button>(R.id.btn_join)
        val btn_create = this.findViewById<Button>(R.id.btn_create)

        // Access a Cloud Firestore instance from your Activity
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        val userid = user?.uid // email?.split("@")?.get(0).toString()

        btn_join.setOnClickListener {
            var username = this.findViewById<EditText>(R.id.user_name).text.toString().trim()
            var familyid = this.findViewById<EditText>(R.id.family_id).text.toString().trim()
            // TODO: 존재하는 family id인지 확인 필요

            //firestore userid와 familyid 문서에 정보 추가
            var new = false // new user인지 확인

            if (userid != null) {
                if (username != null && familyid != null) {
                    // user doc 생성
                    val docRef = db.collection("users").document(userid)
                    val userdata = hashMapOf(
                        "name" to username,
                        "family_id" to familyid
                    )

                    docRef.set(userdata).addOnSuccessListener {
                        Log.d("MY WRITE", "New user add to users Success")
                    }

                    // family doc 갱신: member 추가
                    val docFam = db.collection("family").document(familyid)
                    docFam.get().addOnSuccessListener { result ->
                        var members:ArrayList<Any?> = result.data?.get("members") as ArrayList<Any?>
                        members.add(userid)
                        docFam.update(mapOf(
                            "members" to members,
                        ))

                        Log.d("MY WRITE", "New user add to family Success")
                        Toast.makeText(this, "회원가입에 성공했습니다!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
        }

        btn_create.setOnClickListener {
            val familyid = userid?.let { it1 -> createFamID(it1) }
            var username = this.findViewById<EditText>(R.id.user_name).text.toString().trim()
            var familyname = this.findViewById<EditText>(R.id.family_name).text.toString().trim()

            if (userid != null && username != null && familyid != null) {
                // user doc 생성
                val docRef = db.collection("users").document(userid)
                val userdata = hashMapOf(
                    "name" to username,
                    "family_id" to familyid
                )

                docRef.set(userdata).addOnSuccessListener {
                    Log.d("MY WRITE", "New user add to users Success")
                }

                // family doc 생성
                var members:ArrayList<Any?> = arrayListOf<Any?>(userid)
                val famdata = hashMapOf(
                    "name" to familyname,
                    "members" to members,
                )
                val docFam = db.collection("family").document(familyid)
                docFam.set(famdata).addOnSuccessListener {
                    Log.d("MY WRITE", "Make new family doc Success")
                    Toast.makeText(this, "회원가입에 성공했습니다!", Toast.LENGTH_LONG).show()

                    //회원가입시 posts 생성성
                   val signpost= hashMapOf(
                        "content" to "가입을 축하합니다",
                        "image" to "",
                        "time" to "2021/11/15 22:8",
                        "title" to "가입 축하합니다",
                        "userid" to "관리자"
                    )
                    db.collection("family").document(familyid).collection("posts").document("post_manager").set(signpost)
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }


        }
    }

    private fun createFamID(userId:String): String { // 문서
        val id = "family" + userId

        return id
    }
}