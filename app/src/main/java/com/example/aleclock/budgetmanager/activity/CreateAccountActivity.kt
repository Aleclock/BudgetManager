package com.example.aleclock.budgetmanager.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.aleclock.budgetmanager.account.AccountRowItem
import com.example.aleclock.budgetmanager.MainActivity
import com.example.aleclock.budgetmanager.R
import com.example.aleclock.budgetmanager.transaction.TransactionCategoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    //UI elements
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null

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
        initialize()
    }

    private fun initialize() {
        etFirstName = findViewById<View>(R.id.et_first_name) as EditText
        etLastName = findViewById<View>(R.id.et_last_name) as EditText
        etEmail = findViewById<View>(R.id.et_email) as EditText
        etPassword = findViewById<View>(R.id.et_password) as EditText
        btnCreateAccount = findViewById<View>(R.id.btn_register) as Button

        mDatabase = FirebaseDatabase.getInstance()

        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        btnCreateAccount!!.setOnClickListener { createNewAccount() }

        btn_back.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNewAccount() {

        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (firstName!!.isEmpty() || lastName!!.isEmpty() || email!!.isEmpty() || password!!.isEmpty()) {
            Sneaker.with(this)
                .setTitle(getString(R.string.error_fill_fields))
                .setDuration(2000)
                .sneak(R.color.colorError)
            return
        } else {

            val s = Sneaker.with(this)
                .autoHide(false)
                .setTitle(getString(R.string.creating_account))

            s.sneak(R.color.colorThirdLighter)

            // Creazione di un nuovo account
            mAuth!!
            .createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val userId = mAuth!!.currentUser!!.uid

                    saveUserToDB(userId)
                    updateUI()
                    s.hide()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    Sneaker.with(this)
                        .setTitle(getString(R.string.creating_account_failed))
                        .setDuration(2000)
                        .sneak(R.color.colorError)
                }
            }
        }
    }

    private fun saveUserToDB(userId: String) {
        val currentUserDb = mDatabaseReference!!.child(userId)

        val user =
            User(userId, firstName, lastName, email)

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
        val tag = "defaultTransactionCat"

        if (userId == null) return

        // Expense

        val expenseCategories = resources.getStringArray(R.array.category_expense_array)
        expenseCategories.forEach {
            val reference = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child("expense").push()
            val categoryItem =
                TransactionCategoryItem(
                    it,
                    "expense",
                    reference.key!!
                )
            reference.setValue(categoryItem)
                .addOnSuccessListener {
                    Log.d(tag,reference.key!!)
                }
                .addOnFailureListener {
                    Log.d(tag, "Expense default category NOT added")
                }
        }

        // Income

        val incomeCategories = resources.getStringArray(R.array.category_income_array)
        incomeCategories.forEach {
            val reference = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child("income")
            val categoryItem =
                TransactionCategoryItem(
                    it,
                    "expense",
                    reference.key!!
                )
            reference.push().setValue(categoryItem)
                .addOnSuccessListener {
                    Log.d(tag,"Expense default category added")
                }
                .addOnFailureListener {
                    Log.d(tag, "Expense default category NOT added")
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
        val accountValue = AccountRowItem(
            accountName, accountCategory, accountDescription, reference.key!!, userId,
            balance, income, expense, System.currentTimeMillis()
        )
        reference.setValue(accountValue)
            .addOnSuccessListener {
                Log.d(TAG,"Account created")
            }
            .addOnFailureListener {
                Log.e(TAG, "Account NOT created")
            }
    }

    private fun updateUI() {
        //start next activity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}

// Classe necessaria per il caricamento dei dati dell'utente nel Database Firebase
class User (val uid: String?, val firstName:String?, val lastName:String?, val email:String? )