package com.example.aleclock.budgetmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var userText: TextView? = null
    private var userName: String? = null
    private var userMail: String? = null
    private var btnLogout: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setContentView(R.layout.activity_login)

/*        userText = findViewById<View>(R.id.userName) as TextView
        userName = LoginActivity.getUserFirstName() + " " + LoginActivity.getUserLastName()
        userMail = LoginActivity.getUserMail()

        userText!!.text = userName*/
/*
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)*/

        btnLogout = findViewById<View>(R.id.btn_logout) as Button
        btnLogout!!.setOnClickListener { logoutUser() }
    }

    private fun logoutUser() {
        LoginActivity.signOut()
        Log.d(TAG, "DONNEEEE")
        val intent = Intent(applicationContext,LoginActivity::class.java)
        startActivity(intent)
    }


    companion object {
        var ARG_COURSE_SELECTED = "selectedCourse"
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
