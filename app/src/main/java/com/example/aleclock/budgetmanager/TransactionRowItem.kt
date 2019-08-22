package com.example.aleclock.budgetmanager

class TransactionRowItem (val date: String,
                        val accountId: String,
                        val accountName : String,
                        val category: String,
                        val amount: Float,
                        val transactionType: String) {

    constructor():this("","","","", 0f,"")
}
