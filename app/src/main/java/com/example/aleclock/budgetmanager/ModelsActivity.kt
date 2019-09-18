package com.example.aleclock.budgetmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class ModelsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            super.finish()
        }
    }
}
