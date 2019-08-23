package com.example.aleclock.budgetmanager

import android.text.TextUtils.concat
import android.text.TextUtils.substring
import android.util.Log
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.transaction_row_layout.view.*
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class TransactionItem( val transaction: TransactionRowItem, incomeColor: Int, expenseColor: Int) : Item<ViewHolder>() {

    var incomeColor = incomeColor
    var expenseColor = expenseColor

    override fun getLayout(): Int {
        return R.layout.transaction_row_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_transaction_category.text = transaction.category
        viewHolder.itemView.txt_transaction_account.text = transaction.accountName

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

        viewHolder.itemView.txt_transaction_amount.text = concat("€  ", transaction.amount.toString())
        if (transaction.transactionType == "expense")
            viewHolder.itemView.txt_transaction_amount.setTextColor(expenseColor)
        else
            viewHolder.itemView.txt_transaction_amount.setTextColor(incomeColor)
        // TODO impostare colore in base al tipo

    }

    // TODO https://stackoverflow.com/questions/53848189/format-number-using-decimal-format-in-kotlin
    // TODO https://stackoverflow.com/questions/51958307/upload-multiple-images-and-wait-for-completion-before-returning-android-and-fir

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

}