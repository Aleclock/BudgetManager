package com.example.aleclock.budgetmanager.Models

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.ImageButton
import com.example.aleclock.budgetmanager.CategoryItem
import com.example.aleclock.budgetmanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ModelsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_models)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            super.finish()
        }

        getModels()
    }

    private fun getModels() {
        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) {
            return
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/models").child(userId)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()

                    p0.children.forEach {
                        val modelItem = it.getValue(TransactionModelItem::class.java)
                        if (modelItem != null) {  // Se l'oggetto ottenuto non è nullo
                            adapter.add(ModelItem(modelItem, adapter))
                        }
                    }

                    val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_models)
                    recyclerView.adapter = adapter
                }
            })
        }
    }
}
