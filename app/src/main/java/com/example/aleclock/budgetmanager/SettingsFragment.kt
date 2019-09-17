package com.example.aleclock.budgetmanager


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import kotlin.system.exitProcess

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
        listView.setOnItemClickListener { _, _, _, id ->
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
                    showChangeLang()
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

    // TODO aggiungere banner "l'applicazione verrà riavviata"
    // https://www.youtube.com/watch?v=xxPzi2h0Vvc
    private fun showChangeLang() {
        val listLanguage = resources.getStringArray(R.array.language_array)
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle(resources.getString(R.string.choose_language))
        mBuilder.setSingleChoiceItems(listLanguage,-1) { dialog, which ->
            when (which) {
                0 -> {
                    setLocate("it",context!!)
                    restart()
                }
                1 -> {
                    setLocate("en",context!!)
                    restart()
                }
                2 -> {
                    setLocate("de",context!!)
                    restart()
                }
            }
            dialog.dismiss()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    /**
     * Funzione che fa partire l'activity principale (MainActivity) in modo tale da applicare le modifiche apportate
     */
    private fun restartActivity() {
        /*val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity!!.finish()
        startActivity(intent)*/

        val intent = Intent(activity, MainActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity!!.overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity!!.finish()
        activity!!.overridePendingTransition(0, 0)
        startActivity(intent)
    }

    /**
     * Funzione che riavvia completamente l'app
     */
    private fun restart() {
        Thread.sleep(1500)

        val mStartActivity = Intent(context, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(
            context,
            mPendingIntentId,
            mStartActivity,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val mgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        exitProcess(0)
    }

    companion object {

        fun setLocate(lang: String, context : Context) {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)

            val displayMetrics = context.resources?.displayMetrics

            context.resources?.updateConfiguration(config, displayMetrics)

            val editor = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)?.edit()
            editor?.putString("langSel", lang)
            editor?.apply()
        }
    }
}

