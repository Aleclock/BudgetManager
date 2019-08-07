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
    private var mProgressBar: ProgressDialog? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        /*val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val mDatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val mUserReference= mDatabaseReference.child(uid)
        val aaa = mUserReference.child("firstName")


        // TODO https://stackoverflow.com/questions/56199072/how-to-read-data-from-firebase-in-android-studio-using-kotlin
        Log.d("-------------------", aaa.toString())

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userFirstName = snapshot.child("firstName").value as String
                userLastName = snapshot.child("lastName").value as String

                Log.d("-------------------", userFirstName)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })*/


        if (user != null)
            updateUI()
        else
            initialise()
    }

    private fun initialise() {
        //tvForgotPassword = findViewById<View>(R.id.tv_forgot_password) as TextView
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        btnCreateAccount = findViewById<View>(R.id.btn_register_account) as Button
        mProgressBar = ProgressDialog(this)
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
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("Login User...")
            mProgressBar!!.show()
            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        updateUI()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


    companion object UserValue {

        var user = FirebaseAuth.getInstance().currentUser
        var mAuth = FirebaseAuth.getInstance()



        private var userFirstName: String? = null
        private var userLastName: String? = null


        // TODO in teoria da fuori posso accedere alle variabili del companion object. Quindi fare il listenere nel
        //  oncreate e assegnare alle variabili del companion i valori trovati


        fun getUserMail(): String? {
            return user?.email
        }

        fun getUserFirstName(): String? {
            return userFirstName
        }

        fun getUserLastName(): String? {
            return userLastName
        }

        fun signOut() {
            mAuth?.signOut()
        }
    }

}