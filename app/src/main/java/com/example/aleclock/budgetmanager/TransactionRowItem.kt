package com.example.aleclock.budgetmanager

class TransactionRowItem (val date: String,
                        val account: String,
                        val category: String,
                        val amount: String,
                        val transactionType: String) {

    constructor():this("","","","","")
}
