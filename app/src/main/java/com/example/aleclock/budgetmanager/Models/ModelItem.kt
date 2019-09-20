package com.example.aleclock.budgetmanager.Models

import android.text.TextUtils
import com.example.aleclock.budgetmanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.model_transaction_row_layout.view.*

class ModelItem(
    val item: TransactionModelItem,
    val adapter: GroupAdapter<ViewHolder>,
    val incomeColor: Int,
    val expenseColor: Int
) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.model_transaction_row_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_model_category.text = item.category
        viewHolder.itemView.txt_model_account.text = item.accountName
        viewHolder.itemView.txt_model_amount.text = TextUtils.concat("€  ", item.amount.toString())

        if (item.transactionType == "expense")
            viewHolder.itemView.txt_model_amount.setTextColor(expenseColor)
        else
            viewHolder.itemView.txt_model_amount.setTextColor(incomeColor)

        viewHolder.itemView.btn_remove_model.setOnClickListener {
            val pos = viewHolder.adapterPosition
            removeItem(item.modelId)
            adapter.remove(viewHolder.item)
            adapter.notifyItemRemoved(pos)
        }
    }

    private fun removeItem(modelId: String) {
        val userId = FirebaseAuth.getInstance().uid
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance().getReference("/models")
                .child(userId)
                .child(modelId)

            ref.removeValue()
                .addOnSuccessListener {}
                .addOnFailureListener {}
            // TODO aggiungere popup in qualche modo
        }
    }

}
