package com.example.aleclock.budgetmanager.Models

import android.text.TextUtils
import android.text.TextUtils.concat
import com.example.aleclock.budgetmanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.model_transaction_row_layout.view.*
import java.text.DecimalFormat

class ModelItem(
    private val item: TransactionModelItem,
    private val adapter: GroupAdapter<ViewHolder>,
    private val incomeColor: Int,
    private val expenseColor: Int
) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.model_transaction_row_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_model_category.text = item.category
        viewHolder.itemView.txt_model_account.text = item.accountName

        val df = DecimalFormat("#,##0.00")
        //df.roundingMode = RoundingMode.FLOOR
        val amount = df.format(item.amount)

        viewHolder.itemView.txt_model_amount.text = concat("€  ", amount)

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
