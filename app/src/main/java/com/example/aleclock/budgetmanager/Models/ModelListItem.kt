package com.example.aleclock.budgetmanager.Models

import android.text.TextUtils
import com.example.aleclock.budgetmanager.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.model_transaction_row_layout_list.view.*

class ModelListItem (val item: TransactionModelItem) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.model_transaction_row_layout_list
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_model_list_category.text = item.category
        viewHolder.itemView.txt_model_list_account.text = item.accountName
        viewHolder.itemView.txt_model_list_amount.text = TextUtils.concat("€  ", item.amount.toString())
    }
}
