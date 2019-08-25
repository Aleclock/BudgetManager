package com.example.aleclock.budgetmanager

import android.util.Log
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.transaction_activity_item_layout.view.*

class TransactionDetailItem(val title: String, val value: String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.transaction_activity_item_title.text = title
        viewHolder.itemView.transaction_activity_item_value.text = value
    }

    override fun getLayout(): Int {
        return R.layout.transaction_activity_item_layout
    }
}
