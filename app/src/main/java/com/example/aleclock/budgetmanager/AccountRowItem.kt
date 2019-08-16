package com.example.aleclock.budgetmanager

class AccountRowItem (val name:String,
                   val category: String,
                   val description: String,
                   val id:String,
                   val userId: String,
                   val timeStamp: Long) {


    constructor():this("","","","","",-1)

}