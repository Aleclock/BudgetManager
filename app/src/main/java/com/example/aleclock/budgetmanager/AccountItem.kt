package com.example.aleclock.budgetmanager

import android.util.Log
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.account_row_account_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class AccountItem(val account:AccountRowItem): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // Funzione chiamata per ogni elemento contenuto nella lista
        viewHolder.itemView.account_item_name.text = account.name
        viewHolder.itemView.account_item_category.text = account.category

        val date = getDate(account.timeStamp)
        viewHolder.itemView.txt_creation_date.text = date

        viewHolder.itemView.img_account.setImageResource(R.drawable.ic_add)

    }

    private fun getDate(date: Long): CharSequence {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateF = Date(date)

        Log.d("datadate",date.toString())

        return dateFormat.format(dateF)
    }

    override fun getLayout(): Int {
        return R.layout.account_row_account_layout
    }

}