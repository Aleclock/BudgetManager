package com.example.aleclock.budgetmanager


import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment() {

    var TAG = "AccountFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        fetchAccount()
        // TODO se premo velocemente la schermata "account" l'app va in crash perchè il recyclerview è NULL

        btn_show_dialog.setOnClickListener {
            val dialog = context?.let { it1 -> BottomSheetDialog(it1) }
            val view = layoutInflater.inflate(R.layout.new_account_dialog_layout, null)

            dialog?.window?.attributes!!.windowAnimations = R.style.DialogAnimation

            dialog.setContentView(view)
            dialog.show()

            /**
             * Gestione del dialog spinner per la selezione della categoria del nuovo conto
             */
            val spinner = view.findViewById<Spinner>(R.id.spn_category_new_account)
            val categories = resources.getStringArray(R.array.category_array)
            var categorySelected = categories[0]
            val adapter = ArrayAdapter(context,R.layout.select_dialog_item_material,categories)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    categorySelected = categories[position]
                }
            }

            // Amount text Formatter
            val newAccountBalance = view.findViewById<EditText>(R.id.et_balance_account)
            newAccountBalance.addTextChangedListener(MoneyTextWatcher(newAccountBalance))


            val btn = view.findViewById<Button>(R.id.btn_create_account)
            btn.setOnClickListener {
                val newAccountName = view.findViewById<EditText>(R.id.et_name_account).text

                val balance = newAccountBalance.text.substring(2, newAccountBalance.text.length)
                    .replace(",","")
                createNewAccount(newAccountName.toString(), categorySelected, balance.toFloat())
                dialog.hide()
            }
        }
    }

    private fun fetchAccount() {
        val userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
            return
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/account").child(userId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    // p0 contiene tutti i dati
                    val adapter = GroupAdapter<ViewHolder>()
                    p0.children.forEach {
                        val account = it.getValue(AccountRowItem::class.java)
                        if (account != null) {
                            adapter.add(AccountItem(account,context!!, view!!,activity,adapter))
                        }
                    }
                    recycler_view_account.adapter = adapter
                }
            })
        }
    }

    private fun createNewAccount(
        newAccountName: String,
        accountCategory: String,
        newAccountBalance: Float
    ) {
        val accountDescription = ""
        val userId = FirebaseAuth.getInstance().uid

        val balance = newAccountBalance
        val income = 0f
        val expense = 0f

        if (userId == null) return

        // Crea il nodo "account"
        val reference = FirebaseDatabase.getInstance().getReference("/account").child(userId).push()
        val accountValue = AccountRowItem(newAccountName,accountCategory,accountDescription,reference.key!!,userId, balance,income,expense,System.currentTimeMillis())
        reference.setValue(accountValue)
            .addOnSuccessListener {
                Log.d(TAG,"Account created")
                Sneaker.with(this)
                    .setTitle(getString(R.string.account_created))
                    .setDuration(2000)
                    .sneak(R.color.colorPrimary)
                fetchAccount()

            }
            .addOnFailureListener {
                Sneaker.with(this)
                    .setTitle(getString(R.string.account_not_created))
                    .setDuration(2000)
                    .sneak(R.color.colorError)
            }
    }

}
