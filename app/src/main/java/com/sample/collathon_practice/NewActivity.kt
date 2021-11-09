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

class NewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        val btn_join = this.findViewById<Button>(R.id.btn_join)
        val btn_create = this.findViewById<Button>(R.id.btn_create)

        // Access a Cloud Firestore instance from your Activity
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

//        val user_data = hashMapOf{
//            "name" to "",
//            "family_id" to ""
//        }
//        db.collection("users").document(userid).set(user_data)

        var username = this.findViewById<EditText>(R.id.user_name).text.toString().trim()

        btn_join.setOnClickListener {
            var familyid = this.findViewById<EditText>(R.id.family_id).text.toString().trim()
            // TODO: 존재하는 family id인지 확인 필요

            //firestore userid와 familyid 문서에 정보 추가
            var new = false // new user인지 확인

            if (user != null) {
                val userid = user.uid // email?.split("@")?.get(0).toString()

                if (username != null && familyid != null) {
                    val docRef = db.collection("users").document(userid)
                    val userdata = hashMapOf(
                        "name" to username,
                        "family_id" to familyid
                    )

                    docRef.set(userdata).addOnSuccessListener {
                        Log.d("MY WRITE", "New user add to users Success")
                    }

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
            val familyid = createFamID()
            // TODO: firestore에 userid와 familyid 문서 생성

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun createFamID(): String {
        val id = "family"

        return id
    }
}