package com.sample.collathon_practice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        var btn_join=this.findViewById<Button>(R.id.btn_join)
        var btn_create=this.findViewById<Button>(R.id.btn_create)

        btn_join.setOnClickListener {
            var familyid = this.findViewById<EditText>(R.id.family_id).getText().toString().trim()

            //firestore userid와 familyid 문서에 정보 추가

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btn_join.setOnClickListener {
            val familyid = createFamID()
            // firestore에 userid와 familyid 문서 생성

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createFamID(): String {
        val id = "family"

        return id
    }
}