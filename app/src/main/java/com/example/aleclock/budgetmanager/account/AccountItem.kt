package com.example.aleclock.budgetmanager.account

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import com.example.aleclock.budgetmanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.account_row_account_layout.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class AccountItem(
    val account: AccountRowItem,
    val context: Context,
    val view: View,
    val fragment: FragmentActivity?,
    val adapter: GroupAdapter<ViewHolder>
): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        // Funzione chiamata per ogni elemento contenuto nella lista
        viewHolder.itemView.account_item_name.text = account.name
        viewHolder.itemView.account_item_category.text = account.category

        val df = DecimalFormat("#,##0.00")
        df.roundingMode = RoundingMode.FLOOR
        val balance = df.format(account.balance)

        viewHolder.itemView.txt_balance.text = TextUtils.concat("€  ", balance )

        val date = getDate(account.timeStamp)
        viewHolder.itemView.txt_creation_date.text = date

        val btnDelete = view.findViewById<FloatingActionButton>(R.id.btn_remove_account)
        val btnBack = view.findViewById<FloatingActionButton>(R.id.btn_back_selected)

        // https://stackoverflow.com/questions/46609356/recyclerview-on-item-selection-with-long-click?noredirect=1&lq=1
        viewHolder.itemView.setOnLongClickListener{

            val animationIn = AnimationUtils.loadAnimation(context,
                R.anim.slide_up
            )
            val animationOut = AnimationUtils.loadAnimation(context,
                R.anim.slide_down
            )
            btnDelete.startAnimation(animationIn)
            btnBack.startAnimation(animationIn)

            viewHolder.itemView.setBackgroundDrawable(context.resources.getDrawable(R.drawable.custom_account_selected_layout))
            btnDelete.isClickable = true
            btnBack.isClickable = true
            btnDelete.visibility = View.VISIBLE
            btnBack.visibility = View.VISIBLE

            btnDelete.setOnClickListener {
                removeItem(account)

                val pos = viewHolder.adapterPosition
                adapter.remove(viewHolder.item)
                adapter.notifyItemRemoved(pos)

                btnDelete.startAnimation(animationOut)
                btnBack.startAnimation(animationOut)
                btnDelete.visibility = View.INVISIBLE
                btnBack.visibility = View.INVISIBLE
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

    private fun removeItem(account: AccountRowItem) {
        val accountId = account.id
        val userId = FirebaseAuth.getInstance().uid
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

