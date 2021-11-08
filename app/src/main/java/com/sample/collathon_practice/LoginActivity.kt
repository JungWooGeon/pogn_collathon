package com.sample.collathon_practice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var SignButton=this.findViewById<Button>(R.id.SignButton)
        SignButton.setOnClickListener{
            //login 구현 _ 임시로 버튼 누르면 Main으로 넘어가게 해둠
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
