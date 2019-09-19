package com.example.aleclock.budgetmanager.Models

class TransactionModelItem (
                          val accountId: String,
                          val accountName : String,
                          val category: String,
                          val amount: Float,
                          val note : String,
                          val transactionType: String,
                          val userId : String,
                          val modelId : String) {

    constructor():this("","","",0f,"", "","","")
}