package com.sample.collathon_practice

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.sample.collathon_practice.model.Photo
import kotlinx.android.synthetic.main.activity_add.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    var uriPhoto : Uri? = null
    var IMAGE_PICK = 0;

    lateinit var storage: FirebaseStorage
    lateinit var firestore: FirebaseFirestore

    var nickname = "temp"
    var family = "temp_family"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        storage = Firebase.storage
        firestore = FirebaseFirestore.getInstance()

        // action bar hide
        var actionBar : ActionBar?

        actionBar = supportActionBar;
        actionBar?.hide();

        // calendar
        textview_date = this.date
        val year = cal.get(Calendar.YEAR).toString()
        val mon = (cal.get(Calendar.MONTH) + 1).toString()
        val day = cal.get(Calendar.DAY_OF_MONTH).toString()
        val hour = cal.get(Calendar.HOUR_OF_DAY).toString()
        val min = cal.get(Calendar.MINUTE).toString()
        textview_date!!.text = year + "/" + mon + "/" + day + "\t" + hour + ":" + min

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.YEAR, year)
                updateDateInView()
            }
        }

        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        textview_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR)).apply{
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
                TimePickerDialog(this@AddActivity,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true).show()
            }
        })



        // '사진 저장' button click
        photo_upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*");
            startActivityForResult(intent,IMAGE_PICK)
        }

        // '업로드' 'button click
        btn_upload.setOnClickListener {
            var title = add_title.text.toString()
            var mainText = add_maintext.text.toString()
            var date = date.text.toString()


            var imageUrl = it.toString()
            var photo = Photo(title, mainText, date, nickname, family, imageUrl)
            firestore.collection(family).add(photo).addOnSuccessListener {
                uploadImage(it.id)
                Toast.makeText(this, "데이터가 저장되었습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // upload image in firebase storage
    private fun uploadImage(docId: String) {
        val storageRef: StorageReference = storage.reference
        val imgRef: StorageReference = storageRef.child(family+"/${docId}.jpg")
        // file upload
        var file = uriPhoto
        if (file != null) {
            imgRef.putFile(file).addOnFailureListener {
                Log.d("a", "failure " + it)
            }.addOnSuccessListener {
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==IMAGE_PICK&&resultCode== Activity.RESULT_OK){
            uriPhoto=data?.data
        }
    }

    private fun updateDateInView() {
        val myFormat = "yyyy/MM/dd \t hh/mm" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }
}