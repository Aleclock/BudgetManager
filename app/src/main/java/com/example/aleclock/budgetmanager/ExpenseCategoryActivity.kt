package com.example.aleclock.budgetmanager

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ExpenseCategoryActivity : AppCompatActivity()  {

    val categoryType = "expense"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        val title = findViewById<TextView>(R.id.category_transaction_title)
        title.text = resources.getString(R.string.category_expense)

        getData()

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            super.finish()
        }

        val btnNewCategory = findViewById<ImageButton>(R.id.btn_add_transaction_category)
        btnNewCategory.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.new_transaction_category_dialog, null)

            dialog?.window?.attributes!!.windowAnimations = R.style.DialogAnimation

            dialog.setContentView(view)
            dialog.show()

            val btn = view.findViewById<Button>(R.id.btn_create_transaction_category)
            btn.setOnClickListener {
                val newCategoryName = view!!.findViewById<EditText>(R.id.et_name_transaction_category).text
                if (newCategoryName.toString() != "") {
                    createNewCategory(newCategoryName.toString())
                    dialog.hide()
                } else
                    Sneaker.with(this)
                        .setTitle(getString(R.string.mandatory_name_category))
                        .setDuration(2000)
                        .sneak(R.color.colorError)

            }
        }
    }

    private fun createNewCategory(categoryName: String) {
        val userId = FirebaseAuth.getInstance().uid

        if (userId != null) {
            val ref = FirebaseDatabase.getInstance().getReference("/transactionCategory")
                .child(userId)
                .child(categoryType).push()

            val categoryItem = TransactionCategoryItem(categoryName,categoryType,ref.key!!)
            ref.setValue(categoryItem)
                .addOnSuccessListener {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.account_created))
                        .setDuration(2000)
                        .sneak(R.color.colorPrimary)
                    getData()
                }
                .addOnFailureListener {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.account_not_created))
                        .setDuration(2000)
                        .sneak(R.color.colorError)
                }
        }


    }

    private fun getData() {
        val userId = FirebaseAuth.getInstance().uid

/*        val s = Sneaker.with(this)
            .autoHide(false)
            .setTitle(getString(R.string.data_loading))

        s.sneak(R.color.colorThirdLighter)*/

        if (userId == null) {
            return
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/transactionCategory")
                .child(userId)
                .child("/expense")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()

                    p0.children.forEach {
                        val categoryItem = it.getValue(TransactionCategoryItem::class.java)
                        if (categoryItem != null) {  // Se l'oggetto ottenuto non è nullo
                            adapter.add(CategoryItem(categoryItem,adapter))
                        }
                    }

                    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_category)
                    recyclerView.adapter = adapter
                    //s.hide()
                }
            })
        }
    }
}
