package com.example.aleclock.budgetmanager

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.aleclock.budgetmanager.fragment.AccountFragment
import com.example.aleclock.budgetmanager.fragment.GraphFragment
import com.example.aleclock.budgetmanager.fragment.SettingsFragment
import com.example.aleclock.budgetmanager.fragment.TransactionsFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.irozon.sneaker.Sneaker




class MainActivity : AppCompatActivity(), ConnectionReceiver.ConnectionReceiverListener {

    private lateinit var transactionsFragment : TransactionsFragment
    private lateinit var graphFragment: GraphFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var settingsFragment: SettingsFragment

    private var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadLocate()

        setContentView(R.layout.activity_main)

        // FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        baseContext.registerReceiver(ConnectionReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        setConnectionListener(this)

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
                        transactionsFragment =
                            TransactionsFragment()
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
                        accountFragment =
                            AccountFragment()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.frame_layout, accountFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()
                    }

                    R.id.settings -> {
                        settingsFragment =
                            SettingsFragment()
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

    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        val s = Sneaker.with(this)

        if (isConnected) {
/*            s.hide()
            s.setTitle(getString(R.string.connection_established))
            s.sneak(R.color.colorGreen)
            s.autoHide(true)
            s.setDuration(2000)*/
        } else {
            s.autoHide(false)
            .setTitle(getString(R.string.connection_no))
            s.sneak(R.color.colorError)
        }
    }

    /**
     * Funzione che carica la lingua selezionata dall'utente
     */
    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings",Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("langSel","")
        Log.d(",,,", language)
        SettingsFragment.setLocate(language,this)
    }

    private fun setConnectionListener (listener : ConnectionReceiver.ConnectionReceiverListener) {
        ConnectionReceiver.connectionReceiverListener = listener
    }

    companion object {
        var ARG_COURSE_SELECTED = "selectedCourse"
        private var instance: MainActivity? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}
