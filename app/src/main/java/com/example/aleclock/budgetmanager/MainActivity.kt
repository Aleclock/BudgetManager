package com.example.aleclock.budgetmanager

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var transactionsFragment : TransactionsFragment
    private lateinit var graphFragment: GraphFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadLocate()

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

    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings",Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("langSel","")
        SettingsFragment.setLocate(language,this)
    }


    companion object {
        var ARG_COURSE_SELECTED = "selectedCourse"
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
