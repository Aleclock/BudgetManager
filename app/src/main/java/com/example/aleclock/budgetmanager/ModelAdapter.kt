package com.example.aleclock.budgetmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.aleclock.budgetmanager.Models.TransactionModelItem

class ModelAdapter( private val context: Context, private val modelList: ArrayList<TransactionModelItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.model_transaction_row_layout_list, null, true)

            holder.category = convertView!!.findViewById(R.id.txt_model_list_category) as TextView
            holder.account = convertView.findViewById(R.id.txt_model_list_account) as TextView
            holder.amount = convertView.findViewById(R.id.txt_model_list_amount) as TextView

            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.category!!.text = modelList[position].category
        holder.account!!.text = modelList[position].accountName
        holder.amount!!.text = modelList[position].amount.toString()


        return convertView
    }

    override fun getItem(position: Int): Any {
        return  modelList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return modelList.size
    }

}

private class ViewHolder {

    var category: TextView? = null
    internal var account: TextView? = null
    internal var amount: TextView? = null

}
