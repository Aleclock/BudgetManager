package com.example.aleclock.budgetmanager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.category_transaction_row_layout.view.*

class CategoryItem(
        val category: TransactionCategoryItem,
        val adapter: GroupAdapter<ViewHolder>
    ) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.category_transaction_row_layout
        }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_transaction_category_name.text = category.name


        viewHolder.itemView.btn_remove_transaction_category.setOnClickListener {
            val pos = viewHolder.adapterPosition
            removeItem(category.categoryId,category.categoryType)
            adapter.remove(viewHolder.item)
            adapter.notifyItemRemoved(pos)
        }
    }

    private fun removeItem(categoryId: String, categoryType : String) {
        val userId = FirebaseAuth.getInstance().uid
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance().getReference("/transactionCategory")
                .child(userId)
                .child(categoryType)
                .child(categoryId)
            ref.removeValue()
                .addOnSuccessListener {}
                .addOnFailureListener {}
            // TODO aggiungere popup in qualche modo
        }
    }

}
