package com.example.aleclock.budgetmanager


import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.account_row_account_layout.view.*
import kotlinx.android.synthetic.main.fragment_account.*
import java.text.SimpleDateFormat
import java.util.*

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

        /*val dividerItemDecoration = DividerItemDecoration(recycler_view_account.context,layoutManager.orientation)
        recycler_view_account.addItemDecoration(dividerItemDecoration)*/

        /*val img_account = view.findViewById<Spinner>(R.id.img_account)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_account)*/

        fetchAccount()
        // TODO se premo velocemente la schermata "account" l'app va in crash perchè il recyclerview è NULL

        btn_show_dialog.setOnClickListener {
            val dialog = context?.let { it1 -> BottomSheetDialog(it1) }
            val view = layoutInflater.inflate(R.layout.new_account_dialog_layout, null)

            // Aggiunge l'animazione di entrata e di uscita al popup
            dialog?.window?.attributes!!.windowAnimations = R.style.DialogAnimation

            dialog.setContentView(view)
            dialog.show()

            /**
             * Gestione del dialog spinner per la selezione della categoria del nuovo conto
             */
            val spinner = view.findViewById<Spinner>(R.id.spn_category_new_account)
            val categories = resources.getStringArray(R.array.category_array)
            var category_selected = categories[0]
            val adapter = ArrayAdapter(context,R.layout.select_dialog_item_material,categories)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    category_selected = categories[position]
                }

            }


            val btn = view.findViewById<Button>(R.id.btn_create_account)
            btn.setOnClickListener {
                val newAccountName = view!!.findViewById<EditText>(R.id.et_name_account)
                createNewAccount(newAccountName, category_selected)
                dialog.hide()
            }
        }

        // TODO https://www.youtube.com/watch?v=eu_6gIpQPg8&list=PL0dzCUj1L5JE-jiBHjxlmXEkQkum_M3R-&index=6
    }

    private fun fetchAccount() {
        var userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
            return
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/account").child(userId!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    // p0 contiene tutti i dati
                    val adapter = GroupAdapter<ViewHolder>()
                    p0.children.forEach {
                        val account = it.getValue(AccountRowItem::class.java)
                        if (account != null) {
                            adapter.add(AccountItem(account))
                        }
                    }

                    recycler_view_account.adapter = adapter
                }

            })
        }
    }

    private fun createNewAccount(
        editText: EditText,
        accountCategory: String) {
        val accountName = editText.text.toString()
        val accountDescription = ""
        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return

        // Crea il nodo "account"
        val reference = FirebaseDatabase.getInstance().getReference("/account").child(userId).push()
        val accountValue = AccountRowItem(accountName,accountCategory,accountDescription,reference.key!!,userId,System.currentTimeMillis())
        reference.setValue(accountValue)
            .addOnSuccessListener {
                Log.d(TAG,"Account created")
                fetchAccount()
            }
            .addOnFailureListener {
                Log.e(TAG, "Account NOT created")
            }
        // TODO aggiungere banner di avvenuta creazione/errore
        // TODO decidere se aggiungere utenti con cui condividere il conto

    }

}

class AccountItem(val account:AccountRowItem):Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // Funzione chiamata per ogni elemento contenuto nella lista
        viewHolder.itemView.account_item_name.text = account.name
        viewHolder.itemView.account_item_category.text = account.category

        val date = getDate(account.timeStamp)
        viewHolder.itemView.txt_creation_date.text = date

        viewHolder.itemView.img_account.setImageResource(R.drawable.ic_add)

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
