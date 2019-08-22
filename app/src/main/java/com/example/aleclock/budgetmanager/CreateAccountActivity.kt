package com.example.aleclock.budgetmanager

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAccountActivity : AppCompatActivity() {

    // TODO https://www.behance.net/gallery/61857935/Daily-UI-01-05
    // TODO https://www.behance.net/gallery/82332567/Project-application?tracking_source=for_you_activity
    // TODO https://developer.android.com/guide/navigation/navigation-swipe-view da vedere se farlo o no
    // TODO Sostituire progressDialog

    //UI elements
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"
    //global variables
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        initialise()
    }

    private fun initialise() {
        etFirstName = findViewById<View>(R.id.et_first_name) as EditText
        etLastName = findViewById<View>(R.id.et_last_name) as EditText
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnCreateAccount = findViewById<View>(R.id.btn_register) as Button
        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        btnCreateAccount!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {

        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (firstName!!.isEmpty() || lastName!!.isEmpty() || email!!.isEmpty() || password!!.isEmpty()) {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
            return
        } else {

        mProgressBar!!.setMessage("Registering User...")
        mProgressBar!!.show()

        // TODO verificare se crasha ancora dopo la creazione di un account
        // Creazione di un nuovo account
        mAuth!!
            .createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                mProgressBar!!.hide()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this@CreateAccountActivity, "Account created.",
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = mAuth!!.currentUser!!.uid

                    //Verify Email
                    //verifyEmail();
                    //update user profile information

                    saveUserToFirebaseDatabase(userId)
                    updateUserInfoAndUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@CreateAccountActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserToFirebaseDatabase(userId: String) {
        val currentUserDb = mDatabaseReference!!.child(userId)

        val user = User (userId,firstName,lastName,email)

        currentUserDb.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "User saved to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG,"User NOT saved to Firebase Database")
            }

        setDefaultAccount()
        setDefaultTransactionCategory()
    }

    private fun setDefaultTransactionCategory() {
        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return

        // Expense

        var reference = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child("expense")

        val expense_categories = resources.getStringArray(R.array.category_expense_array)
        expense_categories.forEach {
            var categoryItem = TransactionCategoryItem(it,"expense")
            reference.push().setValue(categoryItem)
                .addOnSuccessListener {
                    Log.d(TAG,"Expense default category added")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Expense default category NOT added")
                }
        }

        // Income

        reference = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child("income")

        val income_categories = resources.getStringArray(R.array.category_income_array)
        income_categories.forEach {
            var categoryItem = TransactionCategoryItem(it,"expense")
            reference.push().setValue(categoryItem)
                .addOnSuccessListener {
                    Log.d(TAG,"Expense default category added")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Expense default category NOT added")
                }
        }
    }

    private fun setDefaultAccount() {
        val accountName = "Default account"
        val accountDescription = ""
        val userId = FirebaseAuth.getInstance().uid
        val categories = resources.getStringArray(R.array.category_array)
        var accountCategory = categories[0]

        val balance = 0f
        val income = 0f
        val expense = 0f

        if (userId == null) return

        // Crea il nodo "account"
        val reference = FirebaseDatabase.getInstance().getReference("/account").child(userId).push()
        val accountValue = AccountRowItem(accountName,accountCategory,accountDescription,reference.key!!,userId,
                                balance, income, expense, System.currentTimeMillis())
        reference.setValue(accountValue)
            .addOnSuccessListener {
                Log.d(TAG,"Account created")
            }
            .addOnFailureListener {
                Log.e(TAG, "Account NOT created")
            }
    }

    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}

// Classe necessaria per il caricamento dei dati dell'utente nel Database Firebase
class User(val uid: String?, val firstName:String?, val lastName:String?, val email:String? )