package com.example.aleclock.budgetmanager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.concat
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*

class TransactionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        var transaction = intent.getParcelableExtra<TransactionRowItem>("transaction")

        var btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            super.finish()
        }

        var txtAmount = findViewById<TextView>(R.id.transaction_detail_amount)
        var txtCategory = findViewById<TextView>(R.id.transaction_detail_category)
        var recycleView = findViewById<RecyclerView>(R.id.recycler_view_transaction_detail)

        txtAmount.text = concat(transaction.amount.toString(), " €")
        txtCategory.text = transaction.category


        val adapter = GroupAdapter<ViewHolder>()

        // android:background="@drawable/custom_transaction_detail_income"
        val container = findViewById<ConstraintLayout>(R.id.transaction_detail_category_amount)
        if (transaction.transactionType == "expense")
            container.background = getDrawable(R.drawable.custom_transaction_detail_expense)
        else
            container.background = getDrawable(R.drawable.custom_transaction_detail_income)

        val date = getFormattedDate(transaction.date)
        adapter.add(TransactionDetailItem("Data",date.toString()))
        adapter.add(TransactionDetailItem("Conto",transaction.accountName))

        if (transaction.note != "")
            adapter.add(TransactionDetailItem("Note",transaction.note))


        recycleView.adapter = adapter
        recycleView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
    }

    private fun getFormattedDate(date: String): Any {
        val fromFormat = SimpleDateFormat("yyyyMMdd")
        val theDate = fromFormat.parse(date.removePrefix("-"))
        val myCal = GregorianCalendar()
        myCal.setTime(theDate)

        val toFormat = SimpleDateFormat("dd.MM.yyyy")
        toFormat.setCalendar(myCal)
        val toDate = toFormat.format(myCal.time)
        return toDate
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {

        return super.onCreateView(name, context, attrs)
    }
}
