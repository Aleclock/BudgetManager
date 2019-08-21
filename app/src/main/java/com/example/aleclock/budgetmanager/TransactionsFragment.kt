package com.example.aleclock.budgetmanager


import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import kotlinx.android.synthetic.main.transaction_row_layout.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class TransactionsFragment : Fragment() {

    var TAG = "TransactionsFragment"

    private var mProgressBar: ProgressDialog? = null

    var tabLayout: TabLayout? = null
    var tabLayoutPeriod: TabLayout? = null
    var transactionType : String = ""

    var accountListItems = ArrayList<String>()
    var accountItemList = ArrayList<AccountRowItem>()
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

        // TODO caricare dati e poi settare gli ascoltatori
        initializeData()

        /**
         * Gestione dei tab per la selezione del periodo (giornaliero, settimanale, mensile)
         */
        tabLayoutPeriod = view?.findViewById<TabLayout>(R.id.tab_layout_period)
        tabLayoutPeriod!!.addTab(tabLayoutPeriod!!.newTab().setText(R.string.daily))
        tabLayoutPeriod!!.addTab(tabLayoutPeriod!!.newTab().setText(R.string.weekly))
        tabLayoutPeriod!!.addTab(tabLayoutPeriod!!.newTab().setText(R.string.monthly))
        tabLayoutPeriod!!.tabGravity = TabLayout.GRAVITY_FILL
        tabLayoutPeriod!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                // Retrieve transaction data from Firebase
                fetchTransaction()
            }

        })

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
                    // TODO quando cambia il tab la categoria selezionata dev'essere rimossa
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
            datePicker_btn.text = getDate(newTransactionDate.time).toString()

            datePicker_btn.setOnClickListener{
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                // TODO i valori iniziali della data sono quelli di oggi (credo)

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
                val newTransactionAmount = view.findViewById<EditText>(R.id.et_amount_transaction).text
                if  (newTransactionAmount.toString() == "")
                    // TODO popup "Errore Inserire importo"
                else {
                    createNewTransaction(
                        getDate(newTransactionDate.time).toString(),
                        acc_category_selected, cat_category_selected,
                        newTransactionAmount.toString(),
                        transactionType
                    )
                    dialog.hide()
                }
            }
        }

    }

    private fun fetchTransaction() {
        var userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
            return
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/transaction").child(userId!!)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val adapter = GroupAdapter<ViewHolder>()

                    p0.children.forEach {
                        val transaction = it.getValue(TransactionRowItem::class.java)
                        if (transaction != null) {
                            adapter.add(TransactionItem(transaction))
                        }
                    }
                    recycler_view_transaction.adapter = adapter
                }

            })
        }

        Log.d("++++","caiaoa")
    }

    private fun initializeData() {
        getAccountlist()
        getCategoryList("expense")
        fetchTransaction()
        initSwipe()
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            // Non è previsto il supporto per lo spostamento verticale
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                // po: viewHolder , p1: direction
                val position = p0.adapterPosition
                if (p1 == ItemTouchHelper.LEFT) {
                    Log.d("onSwiped", "left")
                } else if (p1 == ItemTouchHelper.RIGHT){
                    Log.d("onSwiped", "right")
                }
            }

            var background: RectF = RectF()

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    var icon : Bitmap
                    var itemView : View = viewHolder.itemView
                    var height = itemView.getBottom() - itemView.getTop()
                    var width = height / 3
                    var p = Paint()
                    val corners = 15f


                    if (dX > 0) {

                    //Drawing for Swife Right

                        p.setColor(resources.getColor(R.color.colorThird))
                        background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX/3 ,itemView.bottom.toFloat())
                        c.drawRoundRect(background, corners, corners, p)
                    } else if (dX < 0){

                    //Drawing for Swife Left

                        p.setColor(resources.getColor(R.color.colorError))
                        // TODO il bordo destro è a filo con l'item a differenza dello swipe sinistro
                        background = RectF(itemView.right.toFloat() + dX/3 + 30, itemView.top.toFloat(), itemView.right.toFloat() ,itemView.bottom.toFloat())
                        c.drawRoundRect(background, corners, corners, p)
                    }

                }

                super.onChildDraw(c, recyclerView, viewHolder,  dX/3, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view_transaction)
    }

    private fun getCategoryList(type: String) {
        var userId = FirebaseAuth.getInstance().uid
        if (userId == null) return
        else {
            categoryListItems.clear()
            transactionType = type
            val ref = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child(type)
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
            val ref = FirebaseDatabase.getInstance().getReference("/account").child(userId)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val account = it.getValue(AccountRowItem::class.java)
                        if (account != null) {
                            accountListItems.add(account.id)
                            accountItemList.add(account)
                            // TODO convertire in modo tale da avere i nomi e non l'id
                        }
                    }
                }

            })
        }
    }

    private fun createNewTransaction(
        date: String,
        account: String,
        category: String,
        amount: String,
        transactionType: String) {

        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return
        else {
            val reference = FirebaseDatabase.getInstance().getReference("/transaction").child(userId).push()
            val transactionValue = TransactionRowItem(date, account, category, amount, transactionType)
            reference.setValue(transactionValue)
                .addOnSuccessListener {
                    Log.d(TAG,"Transaction created")
                    fetchTransaction()
                }
                .addOnFailureListener {
                    Log.e(TAG, "Transaction NOT created")
                }

        }

    }

    private fun getDate(date: Long): CharSequence {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateF = Date(date)
        return dateFormat.format(dateF)
    }


}

class TransactionItem(val transaction: TransactionRowItem) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.transaction_row_layout
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txt_transaction_category.text = transaction.category
        viewHolder.itemView.txt_transaction_account.text = transaction.account

        val format = SimpleDateFormat("dd.MM.yyyy")
        val theDate = format.parse(transaction.date)
        val myCal = GregorianCalendar()
        myCal.setTime(theDate)

        val day = myCal.get(Calendar.DAY_OF_MONTH)
        val month = myCal.get(Calendar.MONTH) +1

        viewHolder.itemView.txt_transaction_date_day.text = day.toString()
        viewHolder.itemView.txt_transaction_date_month.text = month.toString()

        viewHolder.itemView.txt_transaction_amount.text = transaction.amount
        // TODO impostare colore in base al tipo
        if (transaction.transactionType == "expense") {
            // Colore rosso
        } else {
            // Colore verde
        }

    }

}
