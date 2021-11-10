package com.sample.collathon_practice.model

class Post(
    val postid: String = "",
    val image: String = "",
    val title: String = "",
    val content: String = "",
    var like: ArrayList<Any?> = arrayListOf<Any?>()
)