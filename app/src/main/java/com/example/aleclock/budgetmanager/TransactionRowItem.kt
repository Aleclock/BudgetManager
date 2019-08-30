package com.example.aleclock.budgetmanager

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TransactionRowItem (val date: String,
                        val accountId: String,
                        val transactionId : String,
                        val accountName : String,
                        val category: String,
                        val amount: Float,
                        val note : String,
                        val transactionType: String,
                        val userId : String) : Parcelable {

    // TODO aggiungere userId
    constructor():this("","","","","", 0f,"","","")
}
