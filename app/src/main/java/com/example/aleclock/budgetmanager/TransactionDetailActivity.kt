package com.example.aleclock.budgetmanager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View

class TransactionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {

        var transaction = intent.getParcelableExtra<TransactionRowItem>("transaction")

        Log.d("TransactionDetail",transaction.amount.toString())


        return super.onCreateView(name, context, attrs)
    }
}
