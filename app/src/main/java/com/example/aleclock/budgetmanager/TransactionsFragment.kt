package com.example.aleclock.budgetmanager

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE
import android.text.TextUtils
import android.util.DisplayMetrics
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
import java.lang.Math.abs
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TransactionsFragment : Fragment() {

    var tabLayout: TabLayout? = null
    var tabLayoutPeriod: TabLayout? = null
    var transactionType : String = ""

    var accountListId = ArrayList<String>()
    var accountListName = ArrayList<String>()

    var currentTabPeriod : String = "daily"
    var currentDateSelected : String = getTodayDate()

    var transactionCategoryEx = ArrayList<String>()
    var transactionCategoryIn = ArrayList<String>()

    val transactionArray = ArrayList<TransactionRowItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO caricare dati e poi settare gli ascoltatori

        setDefaultTransactionCategory()
        initializeData()
        initTitleBarButtons()

        /**
         * Gestione dei tab per la selezione del periodo (giornaliero, settimanale, mensile)
         */
        tabLayoutPeriod = view.findViewById(R.id.tab_layout_period)
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
        val btnAddTransaction = view.findViewById<FloatingActionButton>(R.id.btn_add_transaction)
        btnAddTransaction.setOnClickListener {

            if ((transactionCategoryEx.size == 0 && transactionCategoryIn.size == 0) || (accountListId.size == 0)) {
                if (transactionCategoryEx.size == 0 && transactionCategoryIn.size == 0) {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.wait_category_loading))
                        .setDuration(2000)
                        .sneak(R.color.colorError)
                } else {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.error_no_account))
                        .setDuration(2000)
                        .sneak(R.color.colorError)
                }
            } else {
                val dialog = context?.let { it1 -> BottomSheetDialog(it1) }
                val view = layoutInflater.inflate(R.layout.new_transaction_dialog_layout, null)

                // Aggiunge l'animazione di entrata e di uscita al popup
                dialog?.window?.attributes!!.windowAnimations = R.style.DialogAnimation

                dialog.setContentView(view)
                dialog.show()

                /**
                 * Gestione del dialog spinner per la selezione della categoria della nuova transazione
                 */
                val cat_spinner = view.findViewById<Spinner>(R.id.spn_transaction_category)
                var transactionCategories = transactionCategoryEx
                var transactionCategoriesSel = transactionCategories[0]
                var categoryAdapter = ArrayAdapter(
                    context,
                    R.layout.select_dialog_item_material,
                    transactionCategories
                )
                cat_spinner.adapter = categoryAdapter

                cat_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        transactionCategoriesSel = transactionCategories[position]
                    }

                }

                /**
                 * Gestione dei tab del dialog della nuova transazione
                 */
                tabLayout = view?.findViewById(R.id.tab_layout)

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
                                0 -> {
                                    transactionCategories = transactionCategoryEx
                                    transactionCategoriesSel = transactionCategories[0]
                                    transactionType = "expense"
                                    categoryAdapter = ArrayAdapter(
                                        context,
                                        R.layout.select_dialog_item_material,
                                        transactionCategories
                                    )
                                    cat_spinner.adapter = categoryAdapter
                                }
                                1 -> {
                                    transactionCategories = transactionCategoryIn
                                    transactionCategoriesSel = transactionCategories[0]
                                    transactionType = "income"
                                    categoryAdapter = ArrayAdapter(
                                        context,
                                        R.layout.select_dialog_item_material,
                                        transactionCategories
                                    )
                                    cat_spinner.adapter = categoryAdapter
                                }
                            }
                        }
                    }
                })

                /**
                 * Gestione del pulsante per il dataPicker
                 */
                val datePicker_btn = view!!.findViewById<Button>(R.id.btn_date_picker)

                val dateFormat =
                    SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase

                // TODO sostituire con getTodayDate o con la data selezionata corrente
                var newTransactionDate = Calendar.getInstance().time
                var date =
                    dateFormat.format(newTransactionDate)    // newTransactionDate con formato yyyyMMdd

                var newTransactionDateTxt = date
                datePicker_btn.text = getDate(newTransactionDate.time)

                datePicker_btn.setOnClickListener {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    // TODO i valori iniziali della data sono quelli di oggi (credo)

                    val datePickerDialog = DatePickerDialog(
                        activity,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            newTransactionDate =
                                GregorianCalendar(year, monthOfYear, dayOfMonth).time

                            date = dateFormat.format(newTransactionDate)

                            newTransactionDateTxt = date
                            datePicker_btn.text = getDate(newTransactionDate.time)
                        },
                        year,
                        month,
                        day
                    )

                    datePickerDialog.show()
                }


                /**
                 * Gestione del dialog spinner per la selezione del conto della nuova transazione
                 */
                val acc_spinner = view.findViewById<Spinner>(R.id.spn_transaction_type)
                val acc_categories = accountListId
                var acc_category_selected = accountListId[0]
                var acc_category_name_selected = accountListName[0]
                val type_adapter =
                    ArrayAdapter(context, R.layout.select_dialog_item_material, accountListName)
                acc_spinner.adapter = type_adapter

                acc_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        acc_category_selected = acc_categories[position]
                        acc_category_name_selected = accountListName[position]
                    }

                }

                // PLACES AUTOCOMPLETE

                // Amount text Formatter
                val newTransactionAmount = view.findViewById<EditText>(R.id.et_amount_transaction)
                newTransactionAmount.addTextChangedListener(MoneyTextWatcher(newTransactionAmount))


                val btnCreateTransaction = view.findViewById<Button>(R.id.btn_create_transaction)
                btnCreateTransaction.setOnClickListener {
                    val newTransactionNote =
                        view.findViewById<EditText>(R.id.et_description_transaction).text.toString()
                    if (newTransactionAmount.text.toString() == "")
                        Sneaker.with(this)
                            .setTitle(getString(R.string.error_insert_amount))
                            .setDuration(2000)
                            .sneak(R.color.colorError)
                    else {

                        val amount = newTransactionAmount.text.substring(2, newTransactionAmount.text.length)
                            .replace(",","")

                        createNewTransaction(
                            "-$newTransactionDateTxt",
                            acc_category_selected,
                            acc_category_name_selected,
                            transactionCategoriesSel,
                            amount.toFloat(),
                            newTransactionNote,
                            transactionType
                        )
                        dialog.hide()
                    }
                }
            }
        }
    }

    private fun initializeData() {
        getAccountlist()
        transactionCategoryEx = getCategoryList("expense")
        transactionCategoryIn = getCategoryList("income")
        transactionType = "expense"
        fetchTransaction(getTodayDate(), "daily")
        initSwipe()
    }

    private fun getTodayDate(): String {
        val format = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase
        val currentDate = Calendar.getInstance().time
        return format.format(currentDate)
    }

    private fun initTitleBarButtons() {
        val btn_setDate = view!!.findViewById<ImageButton>(R.id.btn_set_date)
        //val btn_filter = view!!.findViewById<ImageButton>(R.id.btn_period_filter)

        /**
         * DatePicker
         */
        btn_setDate.setOnClickListener {

            // Viene settato il datepicker

            val dateFormat = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase

            // TODO sostituire con getTodayDate
            var periodDate = Calendar.getInstance().time
            var date = dateFormat.format(periodDate)    // newTransactionDate con formato yyyyMMdd

            //var periodDatetxt = date
            //datePicker_btn.text = getDate(newTransactionDate.time)

            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


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
/*        btn_filter.setOnClickListener {
            Sneaker.with(this)
                .setTitle("Filtra le categorie")
                .setDuration(2000)
                .sneak(R.color.colorGreen)
        }*/
    }

    /**
     * Funzione che carica le transazioni salvate su Firebase e le inserisce nell'adapter (ordine decrescente in base alla data)
     */
    private fun fetchTransaction(periodDate: String, periodRange: String) {

        val userId = FirebaseAuth.getInstance().uid

        var incomeAmount = 0f
        var expenseAmount = 0f

        val incomeColor = resources.getColor(R.color.colorGreenDark)
        val expenseColor = resources.getColor(R.color.colorError)

        transactionArray.clear()

        if (userId == null) {
            return
        } else {

            setPeriodBarDate(periodDate,periodRange)

            // Variabili per il conteggio delle spese/guadagni riferite al periodo mostrato (giornaliero/mensile)

            val ref = FirebaseDatabase.getInstance().getReference("/transaction").child(userId).orderByChild("date")
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
                                    transactionArray.add(0,transaction)

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
                                    transactionArray.add(0,transaction)

                                    when (transaction.transactionType) {
                                        "expense"   -> expenseAmount -= transaction.amount
                                        "income"    -> incomeAmount += transaction.amount
                                    }
                                }

                            } else {
                                adapter.add(0,TransactionItem(transaction, incomeColor, expenseColor))
                                transactionArray.add(0, transaction)

                                when (transaction.transactionType) {
                                    "expense"   -> expenseAmount -= transaction.amount
                                    "income"    -> incomeAmount += transaction.amount
                                }
                            }
                        }
                    }
                    val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view_transaction)
                    recyclerView?.adapter = adapter
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
        myCal.time = theDate

        val day = myCal.get(Calendar.DAY_OF_MONTH)
        val month = myCal.get(Calendar.MONTH)
        val monthL = DateFormatSymbols().months[month].capitalize()
        val year = myCal.get(Calendar.YEAR)

        val txtPeriodDate = view!!.findViewById<TextView>(R.id.txt_period_date)

        if (periodRange == "daily") {           txtPeriodDate.text = "$day $monthL $year"
        } else if (periodRange == "monthly"){   txtPeriodDate.text = "$monthL $year"
        } else {                                txtPeriodDate.text = ""   }
    }

    private fun setPeriodBarAmount(income: Float, expense: Float) {
        val txtTotIncomeAmount = view?.findViewById<TextView>(R.id.txt_total_income_amount)
        val txtTotExpenseAmount = view?.findViewById<TextView>(R.id.txt_total_expense_amount)
        txtTotExpenseAmount?.text = TextUtils.concat(abs(expense).toString(), "  €")
        txtTotIncomeAmount?.text = TextUtils.concat(income.toString(),"  €")
    }

    // TODO Implementare questa funzione in una classe https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
    private fun initSwipe() {

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            // Non è previsto il supporto per lo spostamento verticale
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                view!!.translationX = 0f
                super.clearView(recyclerView, viewHolder)
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                // po: viewHolder , p1: direction
                val position = p0.adapterPosition   // Mi ritorna la posizione dell'item nel RecyclerView
                val adapter = p0.itemView.parent as RecyclerView

                if (p1 == ItemTouchHelper.LEFT) {
                    removeTransaction(transactionArray[position])
                    transactionArray.removeAt(position)
                    fetchTransaction(currentDateSelected, currentTabPeriod)
                } else if (p1 == ItemTouchHelper.RIGHT){
                    val intent = Intent(context,TransactionDetailActivity::class.java)
                    val transaction = transactionArray[position]
                    intent.putExtra("transaction" , transaction)
                    startActivity(intent)
                    adapter.adapter!!.notifyDataSetChanged()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val displayMetrics = DisplayMetrics()
                activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = displayMetrics.widthPixels

            // Gestione dello swipe (creazione del canvas/rect)

                if (actionState == ACTION_STATE_SWIPE) {

                    val itemView : View = viewHolder.itemView
                    val margin = 150
                    val p = Paint()


                    val background: RectF
                    val icon: Drawable
                    val corner = resources.getDimension(R.dimen.corners)

                    if (dX > 0) {

                //Drawing for Swife Right
                        p.color = resources.getColor(R.color.colorPrimary)
                        icon = resources.getDrawable(R.drawable.ic_info)

                        val top = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                        //val left = itemView.width - icon.intrinsicWidth - (itemView.height - icon.intrinsicHeight) / 2
                        val right = dX.toInt() - margin
                        val left = right - icon.intrinsicWidth
                        val bottom = top + icon.intrinsicHeight

                        icon.setBounds(left, top, right, bottom)

                        //background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.left.toFloat() + dX/3 ,itemView.bottom.toFloat())
                        background = RectF(-corner, itemView.top.toFloat(), dX ,itemView.bottom.toFloat())
                        c.drawRoundRect(background, corner, corner, p)
                        icon.draw(c)
                    } else if (dX < 0){

                //Drawing for Swife Left
                        p.color = resources.getColor(R.color.colorError)
                        icon = resources.getDrawable(R.drawable.ic_delete)

                        val top = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                        val left = width + dX.toInt() + margin
                        val right = left + icon.intrinsicHeight
                        val bottom = top + icon.intrinsicHeight

                        icon.setBounds(left, top, right, bottom)


                        background = RectF(width.toFloat() + dX, itemView.top.toFloat(), width.toFloat() + corner ,itemView.bottom.toFloat())
                        c.drawRoundRect(background, corner, corner, p)
                        icon.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder,  dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view_transaction)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun removeTransaction(transaction: TransactionRowItem) {
        val transactionId = transaction.transactionId
        val userId = FirebaseAuth.getInstance().uid
        if (userId != null) {
            val ref = FirebaseDatabase.getInstance().getReference("/transaction").child(userId).child(transactionId)
            ref.removeValue()
        }
        updateAccountBalance(transaction.accountId,transaction.transactionType,transaction.amount,"restore")
    }

    /**
     * Funzione che scarica da Firebase le categorie delle transizioni del singolo utente
     */
    private fun getCategoryList(type: String) : ArrayList<String> {
        val userId = FirebaseAuth.getInstance().uid
        if (userId == null) return ArrayList(0)
        else {
            var categoryList = ArrayList<String>()
            val ref = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child(type)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val transactionCategory = it.getValue(TransactionCategoryItem::class.java)
                        if (transactionCategory != null) {
                            categoryList.add(transactionCategory.name)
                        }
                    }
                }
            })

            return categoryList
        }
    }

    /**
     * Funzione che scarica da Firebase la lista degli account/conti attivi dell'utente
     */
    private fun getAccountlist() {
        val userId = FirebaseAuth.getInstance().uid
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
        note : String,
        transactionType: String) {

        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return
        else {
            val reference = FirebaseDatabase.getInstance().getReference("/transaction").child(userId).push()

            val transactionValue = TransactionRowItem(date, accountId, reference.key!!, accountName, category, amount, note, transactionType,userId)
            reference.setValue(transactionValue)
                .addOnSuccessListener {
                    Sneaker.with(this)
                        .setTitle(getString(R.string.transaction_created))
                        .setDuration(2000)
                        .sneak(R.color.colorPrimary)

                    updateAccountBalance(accountId, transactionType, amount, "update")
                    fetchTransaction(currentDateSelected, currentTabPeriod)
                }
                .addOnFailureListener {
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
    private fun updateAccountBalance(
        accountId: String,
        transactionType: String,
        amount: Float,
        action: String
    ) {
        val userId = FirebaseAuth.getInstance().uid

        if (userId == null) return
        else {
            val balanceRef = FirebaseDatabase.getInstance().getReference("/account").child(userId).child(accountId).limitToFirst(1)
            balanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val account = p0.getValue(AccountRowItem::class.java)
                    val balance = account!!.balance

                    val reference = FirebaseDatabase.getInstance().getReference("/account").child(userId).child(accountId).child("balance")

                    if (action == "restore") {      // restore (ripristinare saldo già accreditato)
                        if (transactionType == "expense")   // Spesa
                            reference.setValue(balance+amount)
                        else                                // Guadagno
                            reference.setValue(balance-amount)
                    } else {                        // update (aggiornare saldo non accreditato)
                        if (transactionType == "expense")   // Spesa
                            reference.setValue(balance-amount)
                        else                                // Guadagno
                            reference.setValue(balance+amount)
                    }
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


    /**
     * Funzione che genera automaticamente delle categorie delle transazioni. Utilizzata inizialmente dopo aver creato l'account.
     * In questo caso è possibile aggiungerle anche se l'account è già stato creato
     */
    private fun setDefaultTransactionCategory() {
        val userId = FirebaseAuth.getInstance().uid
        val tag = "defaultTransactionCat"

        if (userId == null) return

        // Expense

        val expenseCategories = resources.getStringArray(R.array.category_expense_array)
        expenseCategories.forEach {
            val reference = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child("expense").push()
            val categoryItem = TransactionCategoryItem(it,"expense",reference.key!!)
            reference.setValue(categoryItem)
                .addOnSuccessListener {
                    Log.d(tag,reference.key!!)
                }
                .addOnFailureListener {
                    Log.d(tag, "Expense default category NOT added")
                }
        }

        // Income

        val incomeCategories = resources.getStringArray(R.array.category_income_array)
        incomeCategories.forEach {
            val reference = FirebaseDatabase.getInstance().getReference("/transactionCategory").child(userId).child("income")
            val categoryItem = TransactionCategoryItem(it,"expense",reference.key!!)
            reference.push().setValue(categoryItem)
                .addOnSuccessListener {
                    Log.d(tag,"Expense default category added")
                }
                .addOnFailureListener {
                    Log.d(tag, "Expense default category NOT added")
                }
        }
    }
}
