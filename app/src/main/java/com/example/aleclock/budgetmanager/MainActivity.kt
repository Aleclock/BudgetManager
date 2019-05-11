package com.example.aleclock.budgetmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // setContentView(R.layout.activity_create_account)

        val intent = Intent(this, CreateAccountActivity::class.java)
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
