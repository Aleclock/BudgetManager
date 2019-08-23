package com.example.aleclock.budgetmanager


import android.app.DatePickerDialog
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.irozon.sneaker.Sneaker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_transactions.*
import java.lang.Math.abs
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TransactionsFragment : Fragment() {

    var TAG = "TransactionsFragment"

    var tabLayout: TabLayout? = null
    var tabLayoutPeriod: TabLayout? = null
    var transactionType : String = ""

    var accountListId = ArrayList<String>()
    var accountListName = ArrayList<String>()
    var categoryListItems = ArrayList<String>()

    var currentTabPeriod : String = "daily"
    var currentDateSelected : String = getTodayDate()

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
        initTitleBarButtons()

        /**
         * Gestione dei tab per la selezione del periodo (giornaliero, settimanale, mensile)
         */
        tabLayoutPeriod = view.findViewById<TabLayout>(R.id.tab_layout_period)
        tabLayoutPeriod!!.addTab(tabLayoutPeriod!!.newTab().setText(R.string.daily))
        tabLayoutPeriod!!.addTab(tabLayoutPeriod!!.newTab().setText(R.string.monthly))
        tabLayoutPeriod!!.addTab(tabLayoutPeriod!!.newTab().setText(R.string.total))
        tabLayoutPeriod!!.tabGravity = TabLayout.GRAVITY_FILL
        tabLayoutPeriod!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                // Retrieve transaction data from Firebase
                fetchTransaction(getTodayDate(), "daily")
                if (p0 != null) {
                    when (p0.position) {
                        0 -> {
                            currentTabPeriod = "daily"
                            fetchTransaction(currentDateSelected,currentTabPeriod)  }
                        1 -> {
                            currentTabPeriod = "monthly"
                            fetchTransaction(currentDateSelected,currentTabPeriod)  }
                        2 -> {
                            currentTabPeriod = "total"
                            fetchTransaction(currentDateSelected,currentTabPeriod)  }
                    }
                }
            }

        })

        // TODO se la lista degli account è vuota non si può creare una nuova transizione
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

            // TODO https://stackoverflow.com/questions/33319898/currency-input-with-2-decimal-format
            // TODO https://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext/8275680

            /**
             * Gestione dei tab del dialog della nuova transazione
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

            var dateFormat = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase

            var newTransactionDate = Calendar.getInstance().time
            var date = dateFormat.format(newTransactionDate)    // newTransactionDate con formato yyyyMMdd

            var newTransactionDateTxt = date
            datePicker_btn.text = getDate(newTransactionDate.time)

            datePicker_btn.setOnClickListener{
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                // TODO i valori iniziali della data sono quelli di oggi (credo)

                val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    newTransactionDate = GregorianCalendar(year, monthOfYear, dayOfMonth).time

                    date = dateFormat.format(newTransactionDate)

                    newTransactionDateTxt = date
                    datePicker_btn.text = getDate(newTransactionDate.time)
                }, year, month, day)

                datePickerDialog.show()
            }

            /**
             * Gestione del dialog spinner per la selezione della categoria della nuova transazione
             */
            val cat_spinner = view.findViewById<Spinner>(R.id.spn_transaction_category)
            val cat_categories = categoryListItems
            var cat_category_selected = cat_categories[0]
            val cat_adapter = ArrayAdapter(context,R.layout.select_dialog_item_material,cat_categories)

            cat_spinner.adapter = cat_adapter

            cat_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    cat_category_selected = cat_categories[position]
                }

            }

            /**
             * Gestione del dialog spinner per la selezione del conto della nuova transazione
             */
            val acc_spinner = view.findViewById<Spinner>(R.id.spn_transaction_type)
            val acc_categories = accountListId
            var acc_category_selected = accountListId[0]
            var acc_category_name_selected = accountListName[0]
            val type_adapter = ArrayAdapter(context,R.layout.select_dialog_item_material,accountListName)
            acc_spinner.adapter = type_adapter

            acc_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    acc_category_selected = acc_categories[position]
                    acc_category_name_selected = accountListName[position]
                }

            }


            val btn_create_transaction = view.findViewById<Button>(R.id.btn_create_transaction)
            btn_create_transaction.setOnClickListener {
                val newTransactionAmount = view.findViewById<EditText>(R.id.et_amount_transaction).text.toString()
                if  (newTransactionAmount.toString() == "")
                    Sneaker.with(this)
                        .setTitle(getString(R.string.error_insert_amount))
                        .setDuration(2000)
                        .sneak(R.color.colorError)

                // TODO migliorare
                else {
                    createNewTransaction(
                        "-$newTransactionDateTxt",
                        acc_category_selected,
                        acc_category_name_selected,
                        cat_category_selected,
                        newTransactionAmount.toFloat(),
                        transactionType
                    )
                    dialog.hide()
                }
            }
        }

    }

    private fun initializeData() {
        getAccountlist()
        getCategoryList("expense")
        fetchTransaction(getTodayDate(), "daily")
        initSwipe()
    }

    private fun getTodayDate(): String {
        var format = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase

        var currentDate = Calendar.getInstance().time
        return format.format(currentDate)
    }

    private fun initTitleBarButtons() {
        var btn_setDate = view!!.findViewById<ImageButton>(R.id.btn_set_date)
        var bt_filter = view!!.findViewById<ImageButton>(R.id.btn_filter)


        /**
         * DatePicker
         */
        btn_setDate.setOnClickListener {

            // Viene settato il datepicker

            var dateFormat = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase

            // TODO sostituire con getTodayDate
            var periodDate = Calendar.getInstance().time
            var date = dateFormat.format(periodDate)    // newTransactionDate con formato yyyyMMdd

            //var periodDatetxt = date
            //datePicker_btn.text = getDate(newTransactionDate.time)

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // TODO i valori iniziali della data sono quelli di oggi (credo)

            val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                periodDate = GregorianCalendar(year, monthOfYear, dayOfMonth).time

                currentDateSelected = dateFormat.format(periodDate)

                fetchTransaction(currentDateSelected,currentTabPeriod)
            }, year, month, day)

            datePickerDialog.show()
        }

        /**
         * Filter
         */
        btn_filter.setOnClickListener {
            Sneaker.with(this)
                .setTitle("Filtra le categorie")
                .setDuration(2000)
                .sneak(R.color.colorGreen)
        }
    }

    /**
     * Funzione che carica le transazioni salvate su Firebase e le inserisce nell'adapter (ordine decrescente in base alla data)
     */
    private fun fetchTransaction(periodDate: String, periodRange: String) {

        var userId = FirebaseAuth.getInstance().uid

        var incomeAmount = 0f
        var expenseAmount = 0f

        var incomeColor = resources.getColor(R.color.colorGreenDark)
        var expenseColor = resources.getColor(R.color.colorError)

        if (userId == null) {
            return
        } else {

            setPeriodBarDate(periodDate,periodRange)

            // Variabili per il conteggio delle spese/guadagni riferite al periodo mostrato (giornaliero/mensile)

            val ref = FirebaseDatabase.getInstance().getReference("/transaction").child(userId!!).orderByChild("date")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val adapter = GroupAdapter<ViewHolder>()

                    p0.children.forEach {
                        val transaction = it.getValue(TransactionRowItem::class.java)
                        if (transaction != null) {
                            if (periodRange == "daily") {
                                val dailyDate = transaction.date.removePrefix("-")
                                if (currentDateSelected == dailyDate) {
                                    adapter.add(0,TransactionItem(transaction,incomeColor,expenseColor))

                                    when (transaction.transactionType) {
                                        "expense"   -> expenseAmount -= transaction.amount
                                        "income"    -> incomeAmount += transaction.amount
                                    }
                                }
                            } else if (periodRange == "monthly") {
                                val monthDate = getMonth (transaction.date.removePrefix("-"))
                                val monthCurrent = getMonth (currentDateSelected)

                                if (monthCurrent == monthDate) {
                                    adapter.add(0, TransactionItem(transaction,incomeColor,expenseColor))

                                    when (transaction.transactionType) {
                                        "expense"   -> expenseAmount -= transaction.amount
                                        "income"    -> incomeAmount += transaction.amount
                                    }
                                }
                            } else {
                                adapter.add(0,TransactionItem(
                                    transaction,
                                    incomeColor,
                                    expenseColor
                                ))

                                when (transaction.transactionType) {
                                    "expense"   -> expenseAmount -= transaction.amount
                                    "income"    -> incomeAmount += transaction.amount
                                }
                            }
                        }
                    }
                    recycler_view_transaction.adapter = adapter
                    setPeriodBarAmount (incomeAmount, expenseAmount)
                }
            })
        }
    }

    /**
     * Funzione che converte la data in formato yyyyMMdd a yyyyMM
     */
    private fun getMonth(date: String): String {
        return date.substring(0,6)
    }

    /**
     * Funzione che imposta la date (periodo) riferite al periodo (giorno,mese,totale) selezionato
     */
    private fun setPeriodBarDate(periodDate: String, periodRange: String) {

        val format = SimpleDateFormat("yyyyMMdd")
        val theDate = format.parse(periodDate)
        val myCal = GregorianCalendar()
        myCal.setTime(theDate)

        val day = myCal.get(Calendar.DAY_OF_MONTH)
        val month = myCal.get(Calendar.MONTH)
        val monthL = DateFormatSymbols().months[month].capitalize()
        val year = myCal.get(Calendar.YEAR)

        if (periodRange == "daily") {
            txt_period_date.text = "$day $monthL $year"
        } else if (periodRange == "monthly"){
            txt_period_date.text = "$monthL $year"
        } else {
            txt_period_date.text = ""
        }
    }

    private fun setPeriodBarAmount(income: Float, expense: Float) {
        txt_total_expense_amount.text = TextUtils.concat(abs(expense).toString(), "  €")
        txt_total_income_amount.text = TextUtils.concat(income.toString(),"  €")
    }



    // TODO Implementare questa funzione in una classe
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


    /**
     * Funzione che scarica da Firebase le categorie delle transizioni del singolo utente
     */
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

    /**
     * Funzione che scarica da Firebase la lista degli account/conti attivi dell'utente
     */
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
                            accountListId.add(account.id)
                            accountListName.add(account.name)
                        }
                    }
                }

            })
        }
    }

    /**
     * Funzione che salva in Firebase la nuova transazione
     */
    private fun createNewTransaction(
        date: String,
        accountId: String,
        accountName: String,
        category: String,
        amount: Float,
        transactionType: String) {

        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return
        else {
            val reference = FirebaseDatabase.getInstance().getReference("/transaction").child(userId).push()
            val transactionValue = TransactionRowItem(date, accountId, accountName, category, amount, transactionType)
            reference.setValue(transactionValue)
                .addOnSuccessListener {
                    Log.d("createNewTransaction","Transaction created")

                    Sneaker.with(this)
                        .setTitle(getString(R.string.transaction_created))
                        .setDuration(2000)
                        .sneak(R.color.colorPrimary)

                    updateAccountBalance(accountId,transactionType,amount)
                    fetchTransaction(currentDateSelected, currentTabPeriod)
                }
                .addOnFailureListener {
                    Log.e("createNewTransaction", "Transaction NOT created")

                    Sneaker.with(this)
                        .setTitle(getString(R.string.transaction_not_created))
                        .setDuration(2000)
                        .sneak(R.color.colorError)
                }

        }

    }

    /**
     * Funzione che, dopo aver creato una transazione, aggiorna il valore del saldo dell'account
     */
    private fun updateAccountBalance(accountId: String, transactionType: String, amount: Float) {
        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return
        else {
            val balanceRef = FirebaseDatabase.getInstance().getReference("/account").child(userId).child(accountId).limitToFirst(1)
            balanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val account = p0.getValue(AccountRowItem::class.java)
                    var balance = account!!.balance

                    Log.d("updateAccountBalance",balance.toString())

                    val reference = FirebaseDatabase.getInstance().getReference("/account").child(userId).child(accountId).child("balance")
                    if (transactionType == "expense")   // Spesa
                        reference.setValue(balance-amount)
                    else                                // Guadagno
                        reference.setValue(balance+amount)
                }

            })
        }
    }

    /**
     * Funzione che ritorna la data formattata da inserire come testo nel DatePicker
     */

    // TODO controllare se migliore https://stackoverflow.com/questions/56207152/format-month-of-date-to-string-of-3-first-letters-kotlin
    private fun getDate(date: Long): CharSequence {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val dateF = Date(date)
        return dateFormat.format(dateF)
    }

}
