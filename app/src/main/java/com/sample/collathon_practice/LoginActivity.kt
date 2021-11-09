package com.sample.collathon_practice

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var SignButton=this.findViewById<Button>(R.id.SignButton)
        SignButton.setOnClickListener{
            var email=this.findViewById<EditText>(R.id.email).getText().toString().trim()
            var pwd=this.findViewById<EditText>(R.id.pwd).getText().toString().trim()

            if(email=="" || pwd==""){
                Toast.makeText(applicationContext,"email이나 password를 입력해주세요", Toast.LENGTH_SHORT).show()
            }else{ //회원 가입 o -> 로그인, 회원 가입 x -> 회원가입
                FirebaseAuth
                    .getInstance()
                    .signInWithEmailAndPassword(email,pwd)
                    .addOnCompleteListener(this){task->
                        if(task.isSuccessful){
                            Toast.makeText(applicationContext,"로그인 성공",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else{
                            FirebaseAuth
                                .getInstance()
                                .createUserWithEmailAndPassword(email,pwd)
                                .addOnCompleteListener(this){task ->
                                    if(task.isSuccessful){
                                        Toast.makeText(applicationContext,"가족에 합류하세요!",Toast.LENGTH_SHORT).show()

                                        val intent = Intent(this, NewActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else{
                                        Toast.makeText(applicationContext,"email이나 password를 확인해주세요",Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
            }
        }
    }
}
