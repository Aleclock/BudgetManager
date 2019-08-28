package com.example.aleclock.budgetmanager

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import com.google.firebase.database.FirebaseDatabase
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.account_row_account_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class AccountItem(
    val account: AccountRowItem,
    val context: Context,
    val view: View,
    val recyclerView: RecyclerView,
    val fragment: FragmentActivity?
): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        // Funzione chiamata per ogni elemento contenuto nella lista
        viewHolder.itemView.account_item_name.text = account.name
        viewHolder.itemView.account_item_category.text = account.category
        viewHolder.itemView.txt_balance.text = TextUtils.concat("€  ", account.balance.toString())

        val date = getDate(account.timeStamp)
        viewHolder.itemView.txt_creation_date.text = date

        val btnDelete = view.findViewById<FloatingActionButton>(R.id.btn_remove_account)
        val btnBack = view.findViewById<FloatingActionButton>(R.id.btn_back_selected)

        // TODO https://stackoverflow.com/questions/46609356/recyclerview-on-item-selection-with-long-click?noredirect=1&lq=1
        viewHolder.itemView.setOnLongClickListener{

            val animationIn = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            val animationOut = AnimationUtils.loadAnimation(context, R.anim.slide_down)
            btnDelete.startAnimation(animationIn)
            btnBack.startAnimation(animationIn)

            viewHolder.itemView.setBackgroundDrawable(context.resources.getDrawable(R.drawable.custom_account_selected_layout))
            btnDelete.isClickable = true
            btnBack.isClickable = true
            btnDelete.visibility = View.VISIBLE
            btnBack.visibility = View.VISIBLE

            btnDelete.setOnClickListener {
                removeItem(account,viewHolder.adapterPosition,recyclerView)

                btnDelete.startAnimation(animationOut)
                btnBack.startAnimation(animationOut)
                btnDelete.visibility = View.INVISIBLE
                btnBack.visibility = View.INVISIBLE
                viewHolder.itemView.setBackgroundDrawable(context.resources.getDrawable(R.drawable.custom_account_deleted_layout))
            }

            btnBack.setOnClickListener {
                btnDelete.startAnimation(animationOut)
                btnBack.startAnimation(animationOut)
                btnDelete.visibility = View.INVISIBLE
                btnBack.visibility = View.INVISIBLE
                viewHolder.itemView.setBackgroundDrawable(context.resources.getDrawable(R.drawable.custom_account_layout))
            }
            return@setOnLongClickListener false
        }
    }

    private fun removeItem(
        account: AccountRowItem,
        position: Int,
        recyclerView: RecyclerView
    ) {
        val accountId = account.id
        val userId = account.userId
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance().getReference("/account").child(userId).child(accountId)
            ref.removeValue()
                .addOnSuccessListener {
                    if (fragment != null) {
                        Sneaker.with(fragment)
                            .setTitle(context.resources.getString(R.string.account_deleted))
                            .setDuration(2000)
                            .sneak(R.color.colorPrimary)
                    }

                }
                .addOnFailureListener {
                    if (fragment != null) {
                        Sneaker.with(fragment)
                            .setTitle(context.resources.getString(R.string.account_not_deleted))
                            .setDuration(2000)
                            .sneak(R.color.colorError)
                    }
                }
            //recyclerView.adapter!!.notifyItemRemoved(position)
        }
    }


    private fun getDate(date: Long): CharSequence {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateF = Date(date)
        return dateFormat.format(dateF)
    }

    override fun getLayout(): Int {
        return R.layout.account_row_account_layout
    }

}

