package com.example.aleclock.budgetmanager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.irozon.sneaker.Sneaker

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null

    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null)
            initialise()
        else
            updateUI()
    }

    private fun initialise() {
        //tvForgotPassword = findViewById<View>(R.id.tv_forgot_password) as TextView
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnCreateAccount = findViewById<View>(R.id.btn_register_account) as Button
        /*tvForgotPassword!!
            .setOnClickListener { startActivity(Intent(this@LoginActivity,
                ForgotPasswordActivity::class.java)) }*/
        btnCreateAccount!!
            .setOnClickListener { startActivity(
                Intent(this@LoginActivity,
                CreateAccountActivity::class.java)
            ) }
        btnLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()


        if (email!!.isEmpty() || password!!.isEmpty()) {
            Sneaker.with(this)
                .setTitle(getString(R.string.error_fill_fields))
                .setDuration(2000)
                .sneak(R.color.colorError)
        } else {
            val s = Sneaker.with(this)
                .autoHide(false)
                .setTitle(getString(R.string.logging))

            s.sneak(R.color.colorThirdLighter)

            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                        s.hide()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        s.hide()
                        Sneaker.with(this)
                            .setTitle(getString(R.string.login_failed))
                            .setDuration(2000)
                            .sneak(R.color.colorError)
                    }
                }
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    companion object UserValue {
        var mAuth = FirebaseAuth.getInstance()

        fun signOut() {
            mAuth?.signOut()
        }
    }

}