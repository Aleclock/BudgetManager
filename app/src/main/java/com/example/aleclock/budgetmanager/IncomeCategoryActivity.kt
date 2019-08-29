package com.example.aleclock.budgetmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton

class IncomeCategoryActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)


        var btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            super.finish()
        }
    }
}