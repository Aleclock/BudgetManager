package com.example.aleclock.budgetmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.TextUtils.concat
import android.widget.ImageButton
import android.widget.TextView
import com.example.aleclock.budgetmanager.Models.TransactionModelItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*

class TransactionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        val transaction = intent.getParcelableExtra<TransactionRowItem>("transaction")

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            super.finish()
        }

        val btnSaveModel = findViewById<ImageButton>(R.id.btn_save_model)
        btnSaveModel.setOnClickListener {
            saveModelToDB(transaction)
        }

        val txtAmount = findViewById<TextView>(R.id.transaction_detail_amount)
        val txtCategory = findViewById<TextView>(R.id.transaction_detail_category)
        val recycleView = findViewById<RecyclerView>(R.id.recycler_view_transaction_detail)

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

    // TODO valutare l'idea di creare una classe particolare per questo tipo di dato
    private fun saveModelToDB(transaction: TransactionRowItem) {
        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return
        else {
            val reference = FirebaseDatabase.getInstance().getReference("/models").child(userId).push()

            val model = TransactionModelItem(
                    transaction.accountId,
                    transaction.accountName,
                    transaction.category,
                    transaction.amount,
                    transaction.note,
                    transaction.transactionType,
                    transaction.userId,
                    reference.key!!)

            reference.setValue(model)
                .addOnSuccessListener {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.transaction_created))
                        .setDuration(2000)
                        .sneak(R.color.colorPrimary)
                }
                .addOnFailureListener {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.transaction_not_created))
                        .setDuration(2000)
                        .sneak(R.color.colorError)
                }
        }
    }

    private fun getFormattedDate(date: String): Any {
        val fromFormat = SimpleDateFormat("yyyyMMdd")
        val theDate = fromFormat.parse(date.removePrefix("-"))
        val myCal = GregorianCalendar()
        myCal.time = theDate

        val toFormat = SimpleDateFormat("dd.MM.yyyy")
        toFormat.setCalendar(myCal)
        return toFormat.format(myCal.time)
    }

/*    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {

        return super.onCreateView(name, context, attrs)
    }*/
}
