package com.example.aleclock.budgetmanager


import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GraphFragment : Fragment() {

    var tabLayoutType: TabLayout? = null
    var selectedPeriod = "monthly"
    var selectedCategory = "expense"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // TODO https://github.com/PhilJay/MPAndroidChart

        initTitleBarButtons()

        val pieChart = view!!.findViewById<PieChart>(R.id.pieChart)
        drawPieChart(pieChart)

        // TODO https://spin.atomicobject.com/2018/12/03/kotlin-horizontal-picker-spinner/

        /**
         * Gestione dei tab per la selezione del periodo (giornaliero, settimanale, mensile)
         */
        tabLayoutType = view.findViewById<TabLayout>(R.id.tab_layout_type)
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
                            selectedPeriod = "monthly"
                            drawPieChart(pieChart) }
                        1 -> {
                            selectedPeriod = "yearly"
                            drawPieChart(pieChart) }
                    }
                }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun drawPieChart(pieChart: PieChart) {
        pieChart.isRotationEnabled = false
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        var yVals = getEntry(selectedPeriod,selectedCategory)

    }

    private fun getEntry(period: String, category: String) : Any {
        var data = ArrayList<PieEntry>(0)

        var userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
            return data
        } else {
            val ref = FirebaseDatabase.getInstance().getReference("/transaction").child(userId).
                child("transactionType").equalTo(category)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                }

            })
        }
        return data
    }

    private fun setupBarChartData(pieChart: PieChart) {
        val yVals = ArrayList<PieEntry>()
        yVals.add(PieEntry(30f))
        yVals.add(PieEntry(2f))
        yVals.add(PieEntry(4f))
        yVals.add(PieEntry(22f))
        yVals.add(PieEntry(12.5f))

        val dataSet = PieDataSet(yVals, "")
        dataSet.valueTextSize=0f
        val colors = java.util.ArrayList<Int>()
        colors.add(Color.GRAY)
        colors.add(Color.BLUE)
        colors.add(Color.RED)
        colors.add(Color.GREEN)
        colors.add(Color.MAGENTA)

        dataSet.setColors(colors)
        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.animate()
        pieChart.centerTextRadiusPercent = 0f
        pieChart.isDrawHoleEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
    }

    private fun initTitleBarButtons() {
        var btnFilter = view!!.findViewById<ImageButton>(R.id.btn_filter)
        btnFilter.setOnClickListener {

            val items = arrayOf(getString(R.string.monthly), getString(R.string.yearly))

            val alert = AlertDialog.Builder(context)
            alert.setTitle(getString(R.string.select_period))
            alert.setItems(items) {_, item ->
                when (item) {
                    0 -> {
                        selectedCategory = "expense" }
                    1 -> {
                        selectedCategory = "income" }
                }
            }
            alert.show()
        }
    }

}
