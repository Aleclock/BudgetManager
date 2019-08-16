package com.example.aleclock.budgetmanager


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Logout
         */
        val btnLogout = view!!.findViewById<Button>(R.id.btn_logout)
        btnLogout!!.setOnClickListener { logoutUser() }
    }

    private fun logoutUser() {
        LoginActivity.signOut()
        val intent = Intent(activity, LoginActivity::class.java)

        // TODO in teoria se cambio i flag cambia anche l'animazione
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
