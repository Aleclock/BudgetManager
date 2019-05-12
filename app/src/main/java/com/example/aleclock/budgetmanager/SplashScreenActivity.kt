package com.example.aleclock.budgetmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(1500)

                    // TODO aggiungere controlli firebase (se loggato o no)

                    val intent = Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        }

        background.start()
    }
}