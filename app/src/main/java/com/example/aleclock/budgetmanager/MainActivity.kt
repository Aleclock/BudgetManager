package com.example.aleclock.budgetmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var userText: TextView? = null
    private var userName: String? = null
    private var userMail: String? = null

    lateinit var transactionsFragment : TransactionsFragment
    lateinit var graphFragment: GraphFragment
    lateinit var accountFragment: AccountFragment
    lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Fragment bottom navigation
         */
        val bottomNavigation : BottomNavigationView = findViewById(R.id.nav_bar)

        transactionsFragment = TransactionsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, transactionsFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            if (bottomNavigation.selectedItemId != item.itemId) {
                when (item.itemId) {
                    R.id.transaction -> {
                        transactionsFragment = TransactionsFragment()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, transactionsFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }

                    R.id.graph -> {
                        graphFragment = GraphFragment()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, graphFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }

                    R.id.account -> {
                        accountFragment = AccountFragment()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, accountFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }

                    R.id.settings -> {
                        settingsFragment = SettingsFragment()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, settingsFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }
                }
            }

            true
        }
    }


    companion object {
        var ARG_COURSE_SELECTED = "selectedCourse"
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
