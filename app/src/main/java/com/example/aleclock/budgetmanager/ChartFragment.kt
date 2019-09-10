package com.example.aleclock.budgetmanager


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.irozon.sneaker.Sneaker
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GraphFragment : Fragment() {

    var tabLayoutType: TabLayout? = null
    var selectedPeriod = "monthly"
    var selectedCategory = "expense"
    var currentDateSelected : String = getTodayDate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // https://github.com/PhilJay/MPAndroidChart

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        val barChart = view.findViewById<BarChart>(R.id.negative_positive_chart)


        setDateBar(currentDateSelected,selectedPeriod)
        initTitleBarButtons(pieChart,barChart)
        getEntry(selectedPeriod,selectedCategory,pieChart,barChart)

        /**
         * Gestione dei tab per la selezione del periodo (giornaliero, settimanale, mensile)
         */
        tabLayoutType = view.findViewById(R.id.tab_layout_type)
        tabLayoutType!!.addTab(tabLayoutType!!.newTab().setText(R.string.expenses))
        tabLayoutType!!.addTab(tabLayoutType!!.newTab().setText(R.string.incomes))
        tabLayoutType!!.tabGravity = TabLayout.GRAVITY_FILL
        tabLayoutType!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    when (p0.position) {
                        0 -> {
                            selectedCategory = "expense"
                            getEntry(selectedPeriod, selectedCategory, pieChart, barChart)
                        }
                        1 -> {
                            selectedCategory = "income"
                            getEntry(selectedPeriod, selectedCategory, pieChart, barChart)
                        }
                    }
                }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun getEntry(
        period: String,
        category: String,
        pieChart: PieChart,
        barChart: BarChart
    ){

        val s = Sneaker.with(this)
            .autoHide(false)
            .setTitle(getString(R.string.data_loading))

        s.sneak(R.color.colorThirdLighter)


        val userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
        } else {
            // Credo abbia senso scaricare tutti i dati in una volta sola e poi gestirli
            val ref = FirebaseDatabase.getInstance().getReference("/transaction").child(userId)
            //.child("transactionType").equalTo(category)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val categoryList = HashMap<String, Float>() // Mappa per il grafico a torta

                    // Inizializza un array di Float di dimensioni quelle necessarie con valore 0
                    val expenseList = FloatArray(getXTotal(selectedPeriod,currentDateSelected)){0f }
                    val incomeList = FloatArray(getXTotal(selectedPeriod,currentDateSelected)){0f }

                    p0.children.forEach {
                        val transaction = it.getValue(TransactionRowItem::class.java)

                        if (transaction != null) {  // Se l'oggetto ottenuto non è nullo
                            if (period == "monthly") {  // Se il tab period selezionato è "mensile"

                                val monthDate = getMonth(transaction.date.removePrefix("-"))
                                val monthCurrent = getMonth (currentDateSelected)

                                if (monthCurrent == monthDate) {    // Se il mese dell'oggetto coincide con quello corrente

                            // Bar chart
                                    val xValue = getDayIndex(transaction.date.removePrefix("-"))
                                    val yValue = transaction.amount

                                    if (transaction.transactionType == "expense") {
                                        //yValue *= (-1)  // In modo tale da renderlo negativo (in quanto spesa)
                                        expenseList[xValue] -= yValue
                                    } else {
                                        incomeList[xValue] += yValue
                                    }


                                    if (transaction.transactionType == category) {

                            // Pie chart

                                        if (!categoryList.containsKey(transaction.category)) {
                                            categoryList[transaction.category] = transaction.amount
                                        } else {
                                            categoryList[transaction.category] = categoryList[transaction.category]!! + transaction.amount
                                        }
                                    }
                                }
                            } else if (period == "yearly") {    // Se il tab period selezionato è "annuale"

                                val yearDate = getYear(transaction.date.removePrefix("-"))
                                val yearCurrent = getYear (currentDateSelected)

                                if (yearCurrent == yearDate) {      // Se l'anno dell'oggetto coincide con quello corrente

                            // Bar chart
                                    val xValue = getMonthIndex(transaction.date.removePrefix("-"))
                                    val yValue = transaction.amount

                                    if (transaction.transactionType == "expense") {
                                        expenseList[xValue] -= yValue
                                    } else {
                                        incomeList[xValue] += yValue
                                    }

                                    if (transaction.transactionType == category) {

                            // Pie chart

                                        if (!categoryList.containsKey(transaction.category)) {
                                            categoryList[transaction.category] = transaction.amount
                                        } else {
                                            categoryList[transaction.category] = categoryList[transaction.category]!! + transaction.amount
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Qui ha caricato tutti i valori e quindi richiamo le funzioni per disegnare i grafici
                    setupPieChartData (pieChart,categoryList)

                    setupPositiveNegativeData(barChart, listToData(expenseList, incomeList))
                    s.hide()
                }

            })
        }
    }

    /**
     * Funzione che converte l'array delle spese e l'array dei guadagni riferiti al periodo selezionato (mese/anno) in un
     * oggetto di tipo Data
     */
    private fun listToData(expenseList: FloatArray, incomeList: FloatArray): ArrayList<Data> {
        val data = java.util.ArrayList<Data>()
        for (x in expenseList.indices) {
            data.add(Data(x.toFloat(),expenseList[x], x.toString()))
            data.add(Data(x.toFloat(),incomeList[x], x.toString()))
        }
        return data
    }

    /**
     * Funzione che, dato un periodo (mese, anno) e una data calcola
     *  -   se periodo = mese : il numero di giorni in quel dato mese
     *  -   se periodo = anno : il numero di mesi dell'anno (12)
     */
    private fun getXTotal(period: String, date: String): Int {
        val index : Int

        index = if (period == "monthly") {                        // Calcolo numero dei giorni del mese
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, getYear(date).toInt())
            calendar.set(Calendar.MONTH, getMonthIndex(date)-1)   // -1 perchè la numerazione dei mesi parte da 0
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        } else                                                  // Numero dei mesi 12
            12

        return index
    }


    /**
     * Funzione che carica i dati ottenuti da Firebase nel grafico a torta e lo disegna
     */
    private fun setupPieChartData(pieChart: PieChart, categoryList: HashMap<String, Float>) {

    // Impostazione della legenda

        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.xEntrySpace = 7f
        legend.yEntrySpace = 0f
        pieChart.setUsePercentValues(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.setExtraOffsets(50f, 0f, 50f, 5f)
        pieChart.setDrawCenterText(false)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 50f
        pieChart.isDrawHoleEnabled = true
        pieChart.transparentCircleRadius = 50f

        val chartVal = ArrayList<PieEntry>()

        categoryList.forEach { (key, value) ->
            chartVal.add(PieEntry(value,key))
        }

        val dataSet = PieDataSet(chartVal,"")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

    // Impostazione della palette colori

        val colors = java.util.ArrayList<Int>()
        val colorTemplate = resources.getStringArray(R.array.CustomTemplate2)
        for (c in colorTemplate)
            colors.add(Color.parseColor(c))

        dataSet.colors = colors

        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.4f
        dataSet.valueLinePart2Length = 0.6f
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueTextSize(13f)

        pieChart.data = data
        pieChart.centerTextRadiusPercent = 5f
        pieChart.setDrawEntryLabels(false)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.description.isEnabled = false

        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    /**
     * Funzione che carica i dati ottenuti da Firebase nel grafico a PositiveNegative e lo disegna
     */
    private fun setupPositiveNegativeData(barChart: BarChart, data: ArrayList<Data> ) {

        barChart.setBackgroundColor(Color.WHITE)
        barChart.extraTopOffset = -30f
        barChart.extraBottomOffset = 10f
        barChart.extraLeftOffset = 30f
        barChart.extraRightOffset = 30f
        barChart.animateY(1400, Easing.EaseInOutQuad)
        barChart.description.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.isDoubleTapToZoomEnabled = false
        barChart.renderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler, 30f)

    // Asse X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textColor = resources.getColor(R.color.colorSecondaryMedium)
        xAxis.textSize = 13f

        //xAxis.labelCount = data.size/2
        //xAxis.setCenterAxisLabels(true)
       //xAxis.granularity = 1f

    // Asse Y
        val left = barChart.axisLeft
        left.setDrawLabels(false)
        left.spaceTop = 25f
        left.spaceBottom = 25f
        left.setDrawAxisLine(false)
        left.setDrawGridLines(false)
        left.setDrawZeroLine(true) // draw a zero line
        left.zeroLineColor = Color.GRAY
        left.zeroLineWidth = 0.7f


        val values = java.util.ArrayList<BarEntry>()
        val colors = java.util.ArrayList<Int>()

        val red = resources.getColor(R.color.colorError)
        val green = resources.getColor(R.color.colorGreen)

        data.forEach {
            val bEntry = BarEntry(it.xValue,it.yValue)
            values.add(bEntry)
            if (it.yValue >= 0)
                colors.add(green)
            else
                colors.add(red)
        }

        val set = BarDataSet(values, "Values")
        set.colors = colors
        set.setValueTextColors(colors)

        val barData = BarData(set)

        barChart.data = barData
    }


    /**
     * Funzione che inizializza gli ascoltatori per i pulsanti presenti nella barra del titolo (pulsante filtro periodo)
     */
    private fun initTitleBarButtons(pieChart: PieChart, barChart: BarChart) {
        val btnFilter = view!!.findViewById<ImageButton>(R.id.btn_filter)
        val btnDate = view!!.findViewById<ImageButton>(R.id.btn_chart_date)
        btnFilter.setOnClickListener {

            val items = arrayOf(getString(R.string.monthly), getString(R.string.yearly))

            val title = view!!.findViewById<TextView>(R.id.chartPeriod)

            val alert = AlertDialog.Builder(context)
            alert.setTitle(getString(R.string.select_period))
            alert.setItems(items) { _, item ->
                when (item) {
                    0 -> {
                        selectedPeriod = "monthly"
                        title.text = resources.getString(R.string.monthly)
                    }
                    1 -> {
                        selectedPeriod = "yearly"
                        title.text = resources.getString(R.string.yearly)
                    }
                }
                setDateBar(currentDateSelected,selectedPeriod)
                getEntry(selectedPeriod, selectedCategory, pieChart, barChart)
            }
            alert.show()
        }

        btnDate.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase

            val year = getYear(currentDateSelected).toInt()
            val month = getMonthIndex(currentDateSelected)-1
            val day = getDayIndex(currentDateSelected)

            val datePickerDialog = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val periodDate = GregorianCalendar(year, monthOfYear, dayOfMonth).time

                currentDateSelected = dateFormat.format(periodDate)

                setDateBar(currentDateSelected,selectedPeriod)
                getEntry(selectedPeriod, selectedCategory, pieChart, barChart)
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun setDateBar(date: String?, period: String) {
        val txt = view!!.findViewById<TextView>(R.id.txt_graph_period_date)

        val format = SimpleDateFormat("yyyyMMdd")
        val theDate = format.parse(date)
        val myCal = GregorianCalendar()
        myCal.time = theDate

        //val day = myCal.get(Calendar.DAY_OF_MONTH)
        val month = myCal.get(Calendar.MONTH)
        val monthL = DateFormatSymbols().months[month].capitalize()
        val year = myCal.get(Calendar.YEAR)

        if (period == "monthly")
            txt.text = "$monthL $year"
        else
            txt.text = "$year"
    }

    private fun getTodayDate(): String {
        val format = SimpleDateFormat("yyyyMMdd") // Formato per il salvataggio della data su Firebase
        val currentDate = Calendar.getInstance().time
        return format.format(currentDate)
    }

    private fun getMonth(date: String): Int {
        return date.substring(0,6).toInt()
    }

    private fun getYear(date : String) : String {
        return date.substring(0,4)
    }

    private fun getMonthIndex(date: String): Int {
        return date.substring(4,6).toInt()
    }

    private fun getDayIndex(date: String): Int {
        return date.substring(6,8).toInt()
    }


    /**
     * Demo class representing data.
     */
    private inner class Data internal constructor(
        internal val xValue: Float,
        internal val yValue: Float,
        internal val xAxisValue: String
    )

}


