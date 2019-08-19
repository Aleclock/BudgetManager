package com.example.aleclock.budgetmanager


import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.new_transaction_dialog_layout.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class TransactionsFragment : Fragment() {

    var tabLayout: TabLayout? = null
    var transactionType : String = ""

    var accountListItems = ArrayList<String>()
    var categoryListItems = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAccountlist()
        getCategoryList("expense")

        /**
         * Gestione del pulsante per la creazione di una nuova transazione
         */
        var add_transaction_btn = view.findViewById<FloatingActionButton>(R.id.btn_add_transaction)
        add_transaction_btn.setOnClickListener {
            val dialog = context?.let { it1 -> BottomSheetDialog(it1) }
            val view = layoutInflater.inflate(R.layout.new_transaction_dialog_layout, null)

            // Aggiunge l'animazione di entrata e di uscita al popup
            dialog?.window?.attributes!!.windowAnimations = R.style.DialogAnimation

            dialog.setContentView(view)
            dialog.show()

            /**
             * Gestione dei tab della transazione
             */
            tabLayout = view?.findViewById<TabLayout>(R.id.tab_layout)

            tabLayout!!.addTab(tabLayout!!.newTab().setText(R.string.expense))
            tabLayout!!.addTab(tabLayout!!.newTab().setText(R.string.income))
            tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

            tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {
                }

                override fun onTabSelected(p0: TabLayout.Tab?) {

                    if (p0 != null) {
                        when (p0.position) {
                            0 -> getCategoryList("expense")
                            1 -> getCategoryList("income")
                        }
                    }
                }

            })

            /**
             * Gestione del pulsante per il dataPicker
             */
            var datePicker_btn = view!!.findViewById<Button>(R.id.btn_date_picker)

            var newTransactionDate = Calendar.getInstance().time

            datePicker_btn.setOnClickListener{
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    newTransactionDate = GregorianCalendar(year, monthOfYear, dayOfMonth).time
                    datePicker_btn.text = getDate(newTransactionDate.time).toString()
                }, year, month, day)

                datePickerDialog.show()
            }

            /**
             * Gestione del dialog spinner per la selezione della categoria della nuova transazione
             */
            val cat_spinner = view.findViewById<Spinner>(R.id.spn_transaction_category)
            //val cat_categories = resources.getStringArray(R.array.category_array)
            val cat_categories = categoryListItems
            var cat_category_selected = cat_categories[0]
            val cat_adapter = ArrayAdapter(context,R.layout.select_dialog_item_material,cat_categories)

            cat_spinner.adapter = cat_adapter

            cat_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    cat_category_selected = cat_categories[position]
                }

            }

            /**
             * Gestione del dialog spinner per la selezione del conto della nuova transazione
             */
            val acc_spinner = view.findViewById<Spinner>(R.id.spn_transaction_type)
            val acc_categories = accountListItems
            var acc_category_selected = accountListItems[0]
            val type_adapter = ArrayAdapter(context,R.layout.select_dialog_item_material,acc_categories)
            acc_spinner.adapter = type_adapter

            acc_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    acc_category_selected = acc_categories[position]
                }

            }


            val btn_create_transaction = view.findViewById<Button>(R.id.btn_create_transaction)
            btn_create_transaction.setOnClickListener {
                Log.d("---", newTransactionDate.toString())
                //newTransactionDate FATTO
                // TODO ottenere newTransactionAccount
                // TODO ottenere newTransactionCategory
                val newTransactionAmount = view.findViewById<EditText>(R.id.et_amount_transaction)
                createNewTransaction()
            }
        }

    }

    private fun getCategoryList(type: String) {
        var userId = FirebaseAuth.getInstance().uid
        if (userId == null) return
        else {
            categoryListItems.clear()
            val ref = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId!!).child(type)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val transactionCategory = it.getValue(TransactionCategoryItem::class.java)
                        if (transactionCategory != null) {
                            categoryListItems.add(transactionCategory.name)
                        }
                    }
                }

            })
        }
    }

    private fun getAccountlist() {
        var userId = FirebaseAuth.getInstance().uid
        if (userId == null) return
        else {
            val ref = FirebaseDatabase.getInstance().getReference("/account").child(userId!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val account = it.getValue(AccountRowItem::class.java)
                        if (account != null) {
                            accountListItems.add(account.name)
                        }
                    }
                }

            })
        }
    }

    private fun createNewTransaction() {
    }

    private fun getDate(date: Long): CharSequence {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateF = Date(date)
        return dateFormat.format(dateF)
    }


}
