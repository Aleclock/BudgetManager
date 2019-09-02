package com.example.aleclock.budgetmanager

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

class AccountRowItem (val name:String,
                   val category: String,
                   val description: String,
                   val id:String,
                   val userId: String,
                   val balance: Float,
                   val income: Float,
                   val expense: Float,
                   val timeStamp: Long) {

    constructor():this("","","","","", 0f,0f,0f,-1)
}