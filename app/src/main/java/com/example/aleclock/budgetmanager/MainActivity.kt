package com.example.aleclock.budgetmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // TODO quando si fa "indietro" l'app ritorna alla pagina di login (sistemare)


    private val TAG = "MainActivity"
    private var userText: TextView? = null
    private var userName: String? = null
    private var userMail: String? = null
    private var btnLogout: Button? = null

    lateinit var transactionsFragment : TransactionsFragment
    lateinit var graphFragment: GraphFragment
    lateinit var accountFragment: AccountFragment
    lateinit var settingsFragment: SettingsFragment

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

        /**
         * Logout
         */
        btnLogout = findViewById<View>(R.id.btn_logout) as Button
        btnLogout!!.setOnClickListener { logoutUser() }

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

            true
        }
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
