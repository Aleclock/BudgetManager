package com.example.aleclock.budgetmanager


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView



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

        val contact = arrayOf(
                resources.getString(R.string.category_expense),
                resources.getString(R.string.category_income),
                resources.getString(R.string.language))


        val listView = view.findViewById<ListView>(R.id.setting_listview)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(context,android.R.layout.simple_list_item_1,contact)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            when(id.toInt()) {
                0 -> {
                    val intent = Intent(context,ExpenseCategoryActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(context,IncomeCategoryActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    Log.d("aaa","Cambia lista")
                }
            }
        }

        /**
         * Logout
         */
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout!!.setOnClickListener { logoutUser() }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        Log.d("onContextItemSelected", item.toString())
        return super.onContextItemSelected(item)
    }

    private fun logoutUser() {
        LoginActivity.signOut()
        val intent = Intent(activity, LoginActivity::class.java)

        // TODO in teoria se cambio i flag cambia anche l'animazione
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

