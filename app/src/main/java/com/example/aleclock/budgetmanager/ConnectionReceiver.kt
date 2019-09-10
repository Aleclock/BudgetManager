package com.example.aleclock.budgetmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

// https://www.youtube.com/watch?v=AKXKPQrd2qQ
class ConnectionReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val isConnected = checkConnection(context)

        if (connectionReceiverListener != null)
            connectionReceiverListener!!.onNetworkConnectionChanged(isConnected)
    }

    interface ConnectionReceiverListener {
        fun onNetworkConnectionChanged(isConnected : Boolean)
    }

    private fun checkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting)

    }

    companion object {
        var connectionReceiverListener : ConnectionReceiverListener? = null
        val isConnected : Boolean
        get() {
            val cm = MainActivity.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting)
        }
    }

}