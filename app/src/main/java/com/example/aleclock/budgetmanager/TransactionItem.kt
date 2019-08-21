package com.example.aleclock.budgetmanager

import android.text.TextUtils.substring
import android.util.Log
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.transaction_row_layout.view.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class TransactionItem(val transaction: TransactionRowItem) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.transaction_row_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_transaction_category.text = transaction.category
        viewHolder.itemView.txt_transaction_account.text = transaction.account

        val format = SimpleDateFormat("yyyyMMdd")
        val theDate = format.parse(transaction.date.removePrefix("-"))
        val myCal = GregorianCalendar()
        myCal.setTime(theDate)

        val day = myCal.get(Calendar.DAY_OF_MONTH)
        val month = myCal.get(Calendar.MONTH)
        val monthL = substring(getMonth(month).toUpperCase(),0,3)

        viewHolder.itemView.txt_transaction_date_day.text = day.toString()
        viewHolder.itemView.txt_transaction_date_month.text = monthL
        Log.d("mesee", theDate.toString() +" , " + transaction.date.removePrefix("-"))

        viewHolder.itemView.txt_transaction_amount.text = transaction.amount
        // TODO impostare colore in base al tipo
        if (transaction.transactionType == "expense") {
            // Colore rosso
        } else {
            // Colore verde
        }

    }

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

}