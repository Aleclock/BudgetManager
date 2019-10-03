package com.example.aleclock.budgetmanager.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.aleclock.budgetmanager.R
import com.google.firebase.database.FirebaseDatabase


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // TODO quando effettuo il logout, si apre la schermata di login. Se chiudo l'app e la riapro crasha
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)

                    val intent = Intent(applicationContext,
                        LoginActivity::class.java)
                    startActivity(intent)

                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
        }

        background.start()
    }
}